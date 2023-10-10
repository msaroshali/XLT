/*
 * Copyright (c) 2005-2023 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.xlt.report;

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;

import com.xceptance.xlt.api.engine.ActionData;
import com.xceptance.xlt.api.engine.Data;
import com.xceptance.xlt.api.engine.PageLoadTimingData;
import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.engine.TransactionData;
import com.xceptance.xlt.api.report.PostProcessedDataContainer;
import com.xceptance.xlt.api.util.SimpleArrayList;
import com.xceptance.xlt.api.util.XltCharBuffer;
import com.xceptance.xlt.report.mergerules.RequestProcessingRule;
import com.xceptance.xlt.report.mergerules.RequestProcessingRule.ReturnState;
import com.zaxxer.sparsebits.SparseBitSet;

import it.unimi.dsi.util.FastRandom;

/**
 * Parses lines to data records and performs any data record preprocessing that can be done in parallel. Preprocessing
 * also includes executing request merge rules.
 */
class DataParserThread implements Runnable
{
    /**
     * Class logger.
     */
    private static final Log LOG = LogFactory.getLog(DataParserThread.class);

    /**
     * Pattern used to rename the name of Web driver timers generated by FF add-on.
     */
    private static final Pattern WD_TIMER_NAME_PATTERN = Pattern.compile("page_\\d+");

    /**
     * The data record factory.
     */
    private final DataRecordFactory dataRecordFactory;

    /**
     * The dispatcher that coordinates result processing.
     */
    private final Dispatcher dispatcher;

    /**
     * The start time of the report period. Data records generated outside this window will be ignored.
     */
    private final long fromTime;

    /**
     * The end time of the report period. Data records generated outside this window will be ignored.
     */
    private final long toTime;

    /**
     * The general config of the report generator
     */
    private final ReportGeneratorConfiguration config;

    /**
     * Constructor.
     *
     * @param dataRecordFactory
     *            the data record factory
     * @param fromTime
     *            the start time
     * @param toTime
     *            the end time
     * @param requestProcessingRules
     *            the request processing rules
     * @param dispatcher
     *            the dispatcher that coordinates result processing
     * @param removeIndexesFromRequestNames
     *            whether to automatically remove any indexes from request names
     */
    public DataParserThread(final Dispatcher dispatcher, final DataRecordFactory dataRecordFactory, final long fromTime, final long toTime,
                            final ReportGeneratorConfiguration config)
    {
        this.dataRecordFactory = dataRecordFactory;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.dispatcher = dispatcher;
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        // each parser gets its own rules. They are all identical, but don't share state, hence we can more
        // efficiently cache and process
        final List<RequestProcessingRule> requestProcessingRules = config.getRequestProcessingRules();
        final boolean removeIndexes = config.getRemoveIndexesFromRequestNames();

        final double SAMPLELIMIT = 1 / ((double) config.dataSampleFactor);
        final int SAMPLEFACTOR = config.dataSampleFactor;

        // some fix random sequence that is fast and always the same, this might change in the future
        final FastRandom random = new FastRandom(98765111L);

        // ensure that we are not killing everything
        final SparseBitSet allTimeIndex = new SparseBitSet();
        final SparseBitSet actionTimeIndex = new SparseBitSet();

        final SimpleArrayList<XltCharBuffer> csvParseResultBuffer = new SimpleArrayList<>(32);

        while (true)
        {
            try
            {
                // get a chunk of lines
                final DataChunk chunk = dispatcher.retrieveReadData();

                final List<XltCharBuffer> lines = chunk.getLines();

                final String agentName = chunk.getAgentName();
                final String testCaseName = chunk.getTestCaseName();
                final String userNumber = chunk.getUserNumber();
                final boolean collectActionNames = chunk.getCollectActionNames();
                final boolean adjustTimerName = chunk.getAdjustTimerNames();
                final FileObject file = chunk.getFile();

                final long _fromTime = fromTime;
                final long _toTime = toTime;

                int droppedLines = 0;

                // parse the chunk of lines and preprocess the results
                final PostProcessedDataContainer postProcessedData = new PostProcessedDataContainer(lines.size(), SAMPLEFACTOR);

                int lineNumber = chunk.getBaseLineNumber();

                final int size = lines.size();

                for (int i = 0; i < size; i++)
                {
                    Data data = null;

                    try
                    {
                        // parse the data record for minimal data
                        final XltCharBuffer line = lines.get(i);
                        data = dataRecordFactory.createStatistics(line);

                        // we want to reuse that array because it is just temp transport and at the end, we will always
                        // allocate it freshly and might also either allocate too much or have to grow it
                        csvParseResultBuffer.clear();

                        // get us the minimal data aka type and time
                        data.baseValuesFromCSV(csvParseResultBuffer, line);

                        // see if we have to keep it
                        final long time = data.getTime();
                        if (time < _fromTime || time > _toTime)
                        {
                            // nope
                            continue;
                        }

                        // see if we are sampling data values
                        if (SAMPLEFACTOR > 1)
                        {
                            // never drop Transactions
                            if (!(data instanceof TransactionData))
                            {
                                final SparseBitSet timeIndex = data instanceof ActionData ? actionTimeIndex : allTimeIndex;

                                // see if we have data at this second already
                                final int sec = (int) (data.getTime() * 0.001);
                                if (timeIndex.get(sec))
                                {
                                    // ok, we already have something... see if we want to drop it
                                    if (random.nextDoubleFast() > SAMPLELIMIT)
                                    {
                                        droppedLines++;
                                        continue;
                                    }
                                }
                                else
                                {
                                    // mark that this second has a value
                                    timeIndex.set(sec);
                                }
                            }
                        }

                        // finish parsing
                        data.remainingValuesFromCSV(csvParseResultBuffer);
                    }
                    catch (final Exception ex)
                    {
                        final String msg = String.format("Failed to parse data record at line %,d in file '%s': %s\nLine is: ", lineNumber, file, ex, lines.get(i).toString());
                        LOG.error(msg, ex);

                        continue;
                    }

                    // let's see if this data requires post processing aka filtering/merging
                    if (data != null)
                    {
                        data = applyDataAdjustments(data, agentName, testCaseName, userNumber, collectActionNames, chunk, adjustTimerName);

                        // if this is request, filter it aka apply merge rules
                        if (data instanceof RequestData)
                        {
                            final RequestData result = postprocess((RequestData) data, requestProcessingRules, removeIndexes);
                            if (result != null)
                            {
                                postProcessedData.add(result);
                            }
                        }
                        else
                        {
                            // get us a hashcode for later while the cache is warm
                            // for RequestData, we did that already
                            data.getName().hashCode();
                            postProcessedData.add(data);
                        }
                    }

                    lineNumber++;
                }

                // deliver the chunk of parsed data records
                postProcessedData.droppedLines = droppedLines;
                dispatcher.addPostprocessedData(postProcessedData);
            }
            catch (final InterruptedException e)
            {
                break;
            }
        }
    }

    private Data applyDataAdjustments(final Data data, final String agentName, final String testCaseName, final String userNumber,
                                      final boolean collectActionNames, final DataChunk lineChunk, boolean adjustTimerName)
    {
        // set general fields
        data.setAgentName(agentName);
        data.setTransactionName(testCaseName);

        // set special fields / special handling
        if (data instanceof TransactionData)
        {
            final TransactionData td = (TransactionData) data;
            td.setTestUserNumber(userNumber);
        }
        else if (collectActionNames && data instanceof ActionData)
        {
            // store the action name/time for later use
            lineChunk.getActionNames().put(data.getTime(), data.getName());
        }
        else if (adjustTimerName && (data instanceof RequestData || data instanceof PageLoadTimingData))
        {
            // rename web driver requests/custom timers using the previously stored action names
            final Entry<Long, String> entry = lineChunk.getActionNames().floorEntry(data.getTime());
            final String actionName = (entry != null) ? entry.getValue() : "UnknownAction";

            final Matcher m = WD_TIMER_NAME_PATTERN.matcher(data.getName());
            data.setName(m.replaceFirst(actionName));
        }

        return data;

    }

    /**
     * Processes a request according to the configured request processing rules. Currently, this means renaming or
     * discarding requests.
     *
     * @param requestData
     *              the request data record
     * @param requestProcessingRules
     *              the rules to apply
     * @param removeIndexesFromRequestNames
     *              in case we want to clean the name too
     *
     * @return the processed request data record, or <code>null</code> if the data record is to be discarded
     */
    private RequestData postprocess(final RequestData requestData,
                                    final List<RequestProcessingRule> requestProcessingRules,
                                    final boolean removeIndexesFromRequestNames)
    {
        // fix up the name first (Product.1.2 -> Product) if so configured
        // this can likely live in RequestData and act on XltCharBuffer instead String
        if (removeIndexesFromRequestNames)
        {
            // @TODO Chance for more performance here
            String requestName = requestData.getName();

            final int firstDotPos = requestName.indexOf(".");
            if (firstDotPos > 0)
            {
                requestName = requestName.substring(0, firstDotPos);
                requestData.setName(requestName);
            }
        }

        // remember the original name so we can restore it in case request processing fails
        final String originalName = requestData.getName();

        // execute all processing rules one after the other until processing is complete
        final int size = requestProcessingRules.size();
        for (int i = 0; i < size; i++)
        {
            final RequestProcessingRule requestProcessingRule = requestProcessingRules.get(i);

            try
            {
                // request data comes back indirectly modified if needed
                final ReturnState state = requestProcessingRule.process(requestData);
                if (state == ReturnState.DROP)
                {
                    return null;
                }
                else if (state == ReturnState.STOP)
                {
                    break;
                }
            }
            catch (final Throwable t)
            {
                final String msg = String.format("Failed to apply request merge rule: %s\n%s", requestProcessingRule, t);
                LOG.error(msg);

                // restore the request's original name
                requestData.setName(originalName);

                break;
            }
        }

        // ok, we processed all rules for this dataset, get us the final hashcode for the name, because we need that later
        // here the cache is likely still hot, so this is less expensive
        requestData.getName().hashCode();

        return requestData;
    }


}
