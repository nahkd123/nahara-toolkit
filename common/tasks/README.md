# Nahara's Toolkit: Tasks
Async tasking stuffs.

```java
Task<MyData> queryMyData();
Task<Void> uploadMyData(MyData);

MyData data = queryMyData().await(); // Block main thread
queryMyData().afterThatDo(data -> ...); // Callback like Promise#then from JS
queryMyData().andThen(WhateverThisIs::uploadMyData); // "Composing"
queryMyData().onComplete(result -> ...);
Optional<TaskResult<MyData>> now = queryMyData().get(); // Get the result now. Value not present if the task is still running

// Warp result in Task<T>
Task<MyData> a = Task.resolved(new MyData());
Task<MyData> b = Task.failed(new IllegalArgumentException("invaild query keyword"));

Task.async(() -> {
	Thread.sleep(1000);
	return "Hello world!";
}, ForkJoinPool.commonPool()); // Run task in a work pool
```
