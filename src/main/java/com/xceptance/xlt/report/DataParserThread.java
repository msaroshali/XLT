package com.xceptance.xlt.report;

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;

import com.xceptance.common.lang.OpenStringBuilder;
import com.xceptance.common.util.SimpleArrayList;
import com.xceptance.xlt.api.engine.ActionData;
import com.xceptance.xlt.api.engine.Data;
import com.xceptance.xlt.api.engine.PageLoadTimingData;
import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.engine.TransactionData;
import com.xceptance.xlt.report.mergerules.RequestProcessingRule;
import com.xceptance.xlt.report.mergerules.RequestProcessingRuleResult;

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
        final List<RequestProcessingRule> requestProcessingRules = config.getRequestProcessingRules();
        final boolean removeIndexes = config.getRemoveIndexesFromRequestNames();

        while (true)
        {
            try
            {
                // get a chunk of lines
                final DataChunk lineChunk = dispatcher.retrieveReadData();
                final List<OpenStringBuilder> lines = lineChunk.getLines();

                final String agentName = lineChunk.getAgentName();
                final String testCaseName = lineChunk.getTestCaseName();
                final String userNumber = lineChunk.getUserNumber(); 
                final boolean collectActionNames = lineChunk.getCollectActionNames();
                final boolean adjustTimerName = lineChunk.getAdjustTimerNames();
                final FileObject file = lineChunk.getFile();

                final long _fromTime = fromTime;
                final long _toTime = toTime;

                // parse the chunk of lines and preprocess the results
                final PostprocessedDataContainer postProcessedData = new PostprocessedDataContainer(lines.size());

                int lineNumber = lineChunk.getBaseLineNumber();

                final int size = lines.size();
                for (int i = 0; i < size; i++)
                {
                    Data data = parseLine(lines.get(i), lineNumber, file);
                    if (data != null)
                    {
                        if (filterByTime(data, _fromTime, _toTime) == false)
                        {
                            data = applyDataAdjustments(data, agentName, testCaseName, userNumber,
                                                        collectActionNames, lineChunk, adjustTimerName);

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
                                postProcessedData.add(data);
                            }
                        }
                    }

                    lineNumber++;
                }

                // deliver the chunk of parsed data records
                dispatcher.addPostprocessedData(postProcessedData);
            }
            catch (final InterruptedException e)
            {
                break;
            }
        }
    }

    /**
     * Parses the given line to a data record.
     *
     * @param line
     *            the line to parse
     * @param lineNumber
     *            the number of the line in its file (for logging purposes)
     * @param file
     *              the file it came from for error reporting just in case
     * @return the parsed data record
     */
    private Data parseLine(final OpenStringBuilder line, final int lineNumber, final FileObject file)
    {
        try
        {
            // parse the data record
            return dataRecordFactory.createStatistics(line);
        }
        catch (final Exception ex)
        {
            final String msg = String.format("Failed to parse data record at line %,d in file '%s': %s\nLine is: ", lineNumber, file, ex, line.toString());
            LOG.error(msg);
            ex.printStackTrace();

            return null;
        }
    }

    private boolean filterByTime(final Data data, final long fromTime, final long toTime)
    {
        // skip the data record if it was not generated in the given time period
        final long time = data.getTime();
        if (time < fromTime || time > toTime)
        {
            return true;
        }
        else
        {
            return false;
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
     *            the request data record
     * @param removeIndexesFromRequestNames 
     *              in case we want to clean the name too
     * @return the processed request data record, or <code>null</code> if the data record is to be discarded
     */
    private RequestData postprocess(RequestData requestData, 
                                    final List<RequestProcessingRule> requestProcessingRules, 
                                    boolean removeIndexesFromRequestNames)
    {
        // fix up the name first (Product.1.2 -> Product) if so configured
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
                final RequestProcessingRuleResult result = requestProcessingRule.process(requestData);

                requestData = result.requestData;

                if (result.stopRequestProcessing)
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

        return requestData;
    }

    public static class PostprocessedDataContainer
    {
        private final SimpleArrayList<Data> data;

        /**
         * Creation time of last data record.
         */
        private long maximumTime = 0;

        /**
         * Creation time of first data record.
         */
        private long minimumTime = Long.MAX_VALUE;
        

        PostprocessedDataContainer(final int size)
        {
            data = new SimpleArrayList<>(size);
        }

        public SimpleArrayList<Data> getData()
        {
            return data;
        }

        public void add(final Data d)
        {
            // maintain statistics
            final long time = d.getTime();

            minimumTime = Math.min(minimumTime, time);
            maximumTime = Math.max(maximumTime, time);
            
            data.add(d);
        }

        /**
         * Returns the maximum time.
         *
         * @return maximum time
         */
        public final long getMaximumTime()
        {
            return maximumTime;
        }

        /**
         * Returns the minimum time.
         *
         * @return minimum time
         */
        public final long getMinimumTime()
        {
            return (minimumTime == Long.MAX_VALUE) ? 0 : minimumTime;
        }
    }

}
