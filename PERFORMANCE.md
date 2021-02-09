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

