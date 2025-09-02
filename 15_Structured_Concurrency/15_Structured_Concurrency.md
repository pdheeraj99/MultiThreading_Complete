# 15. Structured Concurrency - Taming the Chaos 🌪️

Mawa, welcome to Chapter 15. This is one of the newest and most forward-looking topics in Java. It's a preview feature, but it represents the future of writing safe and reliable concurrent code.

## The Problem: Orphan Threads and Error Handling Nightmares

`ExecutorService` and `CompletableFuture` are powerful, but they encourage a style of programming often called "fire-and-forget." You submit a task, and it runs off on its own. This leads to two major problems:

1.  **Thread Leaks (Orphan Threads):** Imagine a user makes a web request. Your server starts two background tasks to fetch data for them. The user then closes their browser. The original request thread is gone, but what about the two background tasks? They are now **orphans**. They might keep running, fetching data that no one needs, wasting CPU and memory. There is no clear "owner" or "scope" for these threads.

2.  **Complex Error Handling:** If you start 5 tasks in parallel, what happens if one of them fails? You need to manually check the result of all 5 tasks, find the one that failed, and then, crucially, you need to remember to **cancel** the other 4 tasks that are still running. This is very complex and easy to get wrong.

The core problem is: **Our code structure doesn't match our concurrency structure.** A single logical operation is split into multiple threads that don't have a clear relationship, making them hard to manage and reason about.

## The Solution: Structured Concurrency

Structured Concurrency is a new paradigm that aims to fix this. The main idea is simple but powerful:
> If a task splits into multiple concurrent sub-tasks, they must all return to the same place. The lifetime of the sub-tasks is confined to a specific block of code.

This eliminates thread leaks by design. When the block of code exits, you are *guaranteed* that all the threads started within it have terminated.

**The Analogy: The Unsupervised vs. Supervised Group Project**
*   **Unstructured (Old Way):** A teacher tells three students (threads) to work on a project. The students run off and work independently. One student finishes early and leaves. Another gets stuck and gives up but doesn't tell anyone. The third keeps working, not knowing the project has already failed. The teacher has to manually check on each student to figure out the final status. This is chaos.
*   **Structured (New Way):** The teacher tells the students, "You three will work together in this room (`StructuredTaskScope`). You all finish when the *last* one of you finishes. If *any* one of you fails, everyone must stop immediately and report the failure." The entire project has a clear lifetime and a single, unified outcome.

### How it Works: `StructuredTaskScope`

Structured Concurrency is implemented in Java (as a preview feature in JDK 21) with the `StructuredTaskScope` API.

Here is the basic pattern:
```java
// This is a try-with-resources block, so the scope is always closed.
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

    // 1. Fork: Start multiple concurrent sub-tasks.
    // They are "forked" from the main flow.
    Future<String> userFuture = scope.fork(() -> findUser());
    Future<Integer> orderFuture = scope.fork(() -> fetchOrders());

    // 2. Join: Wait for the sub-tasks to complete.
    // This blocks until either both succeed, or one fails.
    scope.join();

    // 3. Handle Results/Errors:
    // If any task failed, this will throw the exception.
    // It also ensures the other task is cancelled.
    scope.throwIfFailed();

    // If we reach here, it means both tasks were successful.
    // We can now safely get their results.
    String user = userFuture.resultNow();
    Integer orderCount = orderFuture.resultNow();

    System.out.println("User: " + user + ", Orders: " + orderCount);

} // When the 'try' block ends, the scope is closed, guaranteeing all threads are terminated.
```

### Key Policies

`StructuredTaskScope` comes with different shutdown policies:
1.  **`ShutdownOnFailure`**: This is the "all or nothing" policy. If any forked task fails, all other running tasks in the scope are immediately cancelled. The entire group of tasks fails together. This is great for when you need all results to proceed.
2.  **`ShutdownOnSuccess`**: This is the "first to finish wins" policy. If you are forking multiple tasks to find the fastest result (e.g., querying multiple redundant services), you can use this policy. As soon as one task succeeds, all other tasks in the scope are cancelled.

## What's Next?

Mawa, you are now at the cutting edge of Java concurrency! Structured Concurrency, combined with Virtual Threads, represents the future of writing simple, scalable, and—most importantly—**reliable** concurrent code in Java.

Next, we will look at a classic concurrency framework that has been in Java for a long time but shares some of the "divide and conquer" ideas we've seen: the **`Fork/Join Framework`**. See you in Chapter 16! 🚀
