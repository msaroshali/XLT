# TODO
* Fast String test and benchmark

## 8 Cores - AMD - Only Real Cores
20191024-143543

### Original
8 cores
42,704,293 records read - 64,555 ms - 661,500 lines/s
8 + 8 cores
42,704,293 records read - 61,270 ms

### Optimized
All - 8+8 cores
42,704,293 records read - 44,426 ms - 961,246 lines/s
All - 8 cores
42,704,293 records read - 50,342 ms - 848,284 lines/s
All - 4 cores
42,704,293 records read - 67,916 ms - 628,781 lines/s

No merge rules
42,704,293 records read - 27,680 ms - 1,542,785 lines/s

No merge rules, no statistics processor
42,704,293 records read - 14,283 ms - 2,989,869 lines/s
42,704,293 records read - 14,035 ms - 3,042,700 lines/s

No merge rules, no statistics processor, no RequestData.setUrl
2,704,293 records read - 9,328 ms - 4,578,076 lines/s

No merge rules, no statistics processor, RequestData.setUrl - no hashCodeOfUrlWithoutFragment
42,704,293 records read - 12,421 ms - 3,438,072 lines/s

No merge rules, no statistics processor, RequestData.setUrl - no UrlUtils.retrieveHostFromUrl
42,704,293 records read - 9,914 ms - 4,307,474 lines/s


### No request data url handling
RequestData.setUrl

## Fails
* Changing `AbstractDataProcessorBasedReportProvider` to use FastHashMap, seems to save on cpu, but the overall runtimes will be longer

## Tuned RequestData to stay with XltCharBuffer instead of copying it
Branch: 02-requestdata-xltcharbuffer

No merge rules, no statistics processor, no code here (only xltcharbuffer stays)
42,704,293 records read - 9,543 ms - 4,474,934 lines/s
42,704,293 records read - 9,297 ms - 4,593,341 lines/s

No merge rules, no statistics processor
42,704,293 records read - 12,187 ms - 3,504,086 lines/s
42,704,293 records read - 11,774 ms - 3,627,000 lines/s
42,704,293 records read - 12,154 ms - 3,513,600 lines/s

No merge rules
42,704,293 records read - 26,865 ms - 1,589,588 lines/s
42,704,293 records read - 26,537 ms - 1,609,236 lines/s

## Single chunk queue take instead of draining the queue
Branch: 03-single-chunks and 02!

No merge rules, no statistics processor
42,704,293 records read - 10,617 ms - 4,022,256 lines/s

No merge rules
42,704,293 records read - 22,592 ms - 1,890,240 lines/s

## Chunksize 1000
No merge rules, no statistics processor
42,704,293 records read - 10,098 ms - 4,228,985 lines/s

No merge rules


## thread reader, parser, no thread statistics
### No statistics
1/1
42,704,293 records read - 32,641 ms - 1,308,302 lines/s
42,704,293 records read - 34,665 ms - 1,231,914 lines/s

2/1
42,704,293 records read - 19,089 ms - 2,237,115 lines/s

4/4
42,704,293 records read - 11,624 ms - 3,673,804 lines/s

8/4
42,704,293 records read - 9,291 ms - 4,596,308 lines/s

8/6
42,704,293 records read - 9,865 ms - 4,328,869 lines/s

8/8
42,704,293 records read - 9,857 ms - 4,332,382 lines/s


## stats proc, 20191024-143543
8 cores, 8 reader, 1 parser, 1000 chunk, 100 queue

EMPTY
42,704,293 records read - 34,035 ms - 1,254,717 lines/s

ALL
42,704,293 records read - 68,121 ms - 626,889 lines/s

com.xceptance.xlt.report.providers.GeneralReportProvider
42,704,293 records read - 38,510 ms - 1,108,914 lines/s

com.xceptance.xlt.report.providers.TransactionsReportProvider
42,704,293 records read - 35,351 ms - 1,208,008 lines/s

com.xceptance.xlt.report.providers.ActionsReportProvider
42,704,293 records read - 36,966 ms - 1,155,232 lines/s

com.xceptance.xlt.report.providers.RequestsReportProvider
42,704,293 records read - 37,523 ms - 1,138,083 lines/s

com.xceptance.xlt.report.providers.CustomTimersReportProvider
42,704,293 records read - 35,628 ms - 1,198,616 lines/s

com.xceptance.xlt.report.providers.ErrorsReportProvider
42,704,293 records read - 36,652 ms - 1,165,129 lines/s

com.xceptance.xlt.report.providers.ResponseCodesReportProvider
42,704,293 records read - 37,076 ms - 1,151,804 lines/s

com.xceptance.xlt.report.providers.ConfigurationReportProvider
42,704,293 records read - 34,626 ms - 1,233,301 lines/s

com.xceptance.xlt.report.providers.AgentsReportProvider
42,704,293 records read - 36,219 ms - 1,179,058 lines/s

com.xceptance.xlt.report.providers.TestReportConfigurationReportProvider
42,704,293 records read - 35,439 ms - 1,205,008 lines/s

com.xceptance.xlt.report.providers.EventsReportProvider
42,704,293 records read - 36,061 ms - 1,184,224 lines/s

com.xceptance.xlt.report.providers.CustomValuesReportProvider
42,704,293 records read - 37,624 ms - 1,135,028 lines/s

com.xceptance.xlt.report.providers.ContentTypesReportProvider
42,704,293 records read - 40,441 ms - 1,055,965 lines/s

com.xceptance.xlt.report.providers.HostsReportProvider
42,704,293 records read - 36,576 ms - 1,167,550 lines/s

com.xceptance.xlt.report.providers.SummaryReportProvider
42,704,293 records read - 39,646 ms - 1,077,140 lines/s

com.xceptance.xlt.report.providers.PageLoadTimingsReportProvider
42,704,293 records read - 35,372 ms - 1,207,291 lines/s


