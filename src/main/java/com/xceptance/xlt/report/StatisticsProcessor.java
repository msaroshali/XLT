/*
 * Copyright (c) 2005-2020 Xceptance Software Technologies GmbH
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.javascript.host.SimpleArray;
import com.xceptance.common.util.SimpleArrayList;
import com.xceptance.xlt.api.engine.Data;
import com.xceptance.xlt.api.report.ReportProvider;

/**
 * Processes parsed data records. Processing means passing a data record to all configured report providers. Since data
 * processing is not thread-safe (yet), there will be only one data processor.
 */
class StatisticsProcessor implements Runnable
{
    /**
     * Class logger.
     */
    private static final Log LOG = LogFactory.getLog(StatisticsProcessor.class);

    /**
     * The dispatcher that coordinates result processing.
     */
    private final Dispatcher dispatcher;

    /**
     * Creation time of last data record.
     */
    private long maximumTime;

    /**
     * Creation time of first data record.
     */
    private long minimumTime;

    /**
     * The configured report providers. An array for less overhead.
     */
    private final ReportProvider[] reportProviders;

    /**
     * A thread limit given from the outside
     */
    private final int threadCount;
    
    
    /**
     * Constructor.
     *
     * @param reportProviders
     *            the configured report providers
     * @param dispatcher
     *            the dispatcher that coordinates result processing
     */
    public StatisticsProcessor(final List<ReportProvider> reportProviders, final Dispatcher dispatcher, int threadCount)
    {
        this.reportProviders = reportProviders.toArray(new ReportProvider[0]);
        this.dispatcher = dispatcher;

        maximumTime = 0;
        minimumTime = Long.MAX_VALUE;
        this.threadCount = threadCount;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        // use the recommendation from the outside
        final ForkJoinPool pool = new ForkJoinPool(threadCount);

        while (true)
        {
            try
            {
                // get a chunk of parsed data records
                final List<Data> dataRecords = dispatcher.getNextParsedDataRecordChunk();

                // submit this to all report providers and each provider does its own loop
                // we assume that they are independent of each other and hence this is ok
                final ForkJoinTask<?>[] tasks = new ForkJoinTask[reportProviders.length];
                for (int i = 0; i < reportProviders.length; i++)
                {
                    final ReportProvider reportProvider = reportProviders[i];

                    // give all data to each process threads for one report provider aka SIMD
                    // single instruction multiple data
                    final ForkJoinTask<?> task = pool.submit(() -> {
                        processDataRecords(reportProvider, dataRecords);
                    });
                    tasks[i] = task;
                }

                maintainStatistics(dataRecords);

                // wait for completion
                for (int i = 0; i < tasks.length; i++)
                {
                    tasks[i].quietlyJoin();
                }

                // one more chunk is complete
                dispatcher.finishedProcessing();
            }
            catch (final InterruptedException e)
            {
                break;
            }
        }

        // clean up
        pool.shutdown();
        try
        {
            // that should not be necessary, but for the argument of it
            pool.awaitTermination(20, TimeUnit.SECONDS);
        }
        catch (InterruptedException e1)
        {
        }
    }

    /**
     * Processes the given data records by passing them to a report provider.
     *
     * @param reportProvider
     *            the report provider
     * @param data
     *            the data records
     */
    private void processDataRecords(final ReportProvider reportProvider, final List<Data> data)
    {
        // process the data
        final int size = data.size();
        for (int i = 0; i < size; i++)
        {
            try
            {
                reportProvider.processDataRecord(data.get(i));
            }
            catch (final Throwable t)
            {
                LOG.warn("Failed to process data record", t);
                System.err.println("\nFailed to process data record: " + t);
            }
        }
    }

    /**
     * Maintain our statistics
     *
     * @param data
     *            the data records
     */
    private void maintainStatistics(final List<Data> data)
    {
        long min = minimumTime;
        long max = maximumTime;

        // process the data
        final int size = data.size();
        for (int i = 0; i < size; i++)
        {
            // maintain statistics
            final long time = data.get(i).getTime();

            min = Math.min(min, time);
            max = Math.max(max, time);
        }

        minimumTime = Math.min(minimumTime, min);
        maximumTime = Math.max(maximumTime, max);
    }
}
