### Interview Questions for Module 14: Structured Concurrency

#### Core Concepts

1.  **Question:** What is "unstructured concurrency," and what are its main drawbacks?
    *   **Answer:** Unstructured concurrency is the traditional "fire-and-forget" model of multithreading where a parent thread can launch child threads that have independent lifecycles. The main drawbacks are:
        *   **Orphaned Threads:** If the parent task is cancelled or finishes, the child threads can continue running, leading to resource leaks.
        *   **Difficult Error Handling:** If a child thread fails, the parent and sibling threads are not automatically notified, leading to complex error propagation logic and potentially inconsistent state.
        *   **Complex Cancellation:** Cancelling a "family" of related tasks is a manual and error-prone process, requiring explicit tracking of all threads.

2.  **Question:** What is the core principle of Structured Concurrency? How does it solve the problems of the unstructured model?
    *   **Answer:** The core principle is that if a task splits into concurrent subtasks, they must all terminate before the main task can proceed. It enforces that the lifetime of concurrent operations is confined to a specific lexical scope (e.g., a `try-with-resources` block). This solves the old problems by:
        *   **Eliminating Orphans:** The scope guarantees that all child threads are terminated when the scope is exited.
        *   **Simplifying Error Handling:** The scope can automatically shut down all sibling tasks if one fails (short-circuiting).
        *   **Trivializing Cancellation:** The entire scope can be cancelled as a single unit.

3.  **Question:** Explain the role of `StructuredTaskScope`. How does it enforce the structure?
    *   **Answer:** `StructuredTaskScope` is the main entry point for using the API. It creates a boundary for the lifetime of a group of concurrent tasks. It enforces structure by:
        *   **Forking:** You can only start subtasks (`fork()`) within the scope.
        *   **Joining:** The parent thread must call `join()` on the scope, which blocks until all tasks are complete or the scope is shut down by a policy.
        *   **Scoped Lifetime:** The `try-with-resources` construct ensures that the scope is properly closed, which in turn guarantees the termination of all subtasks forked within it. The parent thread cannot continue until the `join()` method returns.

#### Policies and Scenarios

4.  **Question:** Describe the `ShutdownOnFailure` policy. What is a practical use case for it?
    *   **Answer:** The `ShutdownOnFailure` policy dictates that if any subtask forked within the scope fails (throws an exception), the scope will immediately cancel all other running subtasks.
    *   **Use Case:** A classic example is a microservices aggregator. Imagine a method that needs to fetch a `User` and their `Order` from two different services to combine them. If the call to the `User` service fails, there is no point in waiting for the `Order` service call to complete. `ShutdownOnFailure` automatically cancels the `Order` service call, saving resources and failing fast.

5.  **Question:** Describe the `ShutdownOnSuccess` policy. What is a practical use case for it?
    *   **Answer:** The `ShutdownOnSuccess` policy dictates that as soon as any one subtask completes successfully, the scope will immediately cancel all other running subtasks and return the result of the successful task.
    *   **Use Case:** This is ideal for redundancy or racing. For example, you might query three different weather service APIs for the current temperature. They may have different response times. You only care about the first one that responds successfully. `ShutdownOnSuccess` gets you the fastest result and immediately cancels the other two pending API calls.

6.  **Question:** Imagine you need to run three tasks. You need to wait for all of them to complete, even if some of them fail. You then want to collect the results of the successful tasks and the exceptions from the failed ones. Which `StructuredTaskScope` policy would you use, and how would you implement this?
    *   **Answer:** Neither `ShutdownOnFailure` nor `ShutdownOnSuccess` is suitable here. For this "wait for all" scenario, you would need to implement a **custom policy** by extending `StructuredTaskScope`. The logic would involve:
        1.  Create a custom scope class: `class CollectAll<T> extends StructuredTaskScope<T> { ... }`
        2.  Inside the scope, you would track the completed futures.
        3.  The `join()` method would wait for all forked tasks to complete.
        4.  After `join()`, you would iterate through the futures, check their state (`Future.state()`), and collect either the result (`Future.resultNow()`) or the exception (`Future.exceptionNow()`) into separate lists.
        *Note: As of JDK 21, `ShutdownOnFailure` and `ShutdownOnSuccess` are the two provided policies. A "collect all" or "wait for all" collector is a common pattern that one might build on top of the base API.*

#### Comparison and Evolution

7.  **Question:** How does Structured Concurrency compare to using `CompletableFuture.allOf()` or `invokeAll()` on an `ExecutorService`? What are its advantages?
    *   **Answer:**
        *   `invokeAll()` blocks and waits for all tasks to complete, but its short-circuiting and cancellation capabilities are limited and not as robust.
        *   `CompletableFuture.allOf()` is non-blocking but leads to more complex, callback-style code (the "pyramid of doom"). Error propagation and cancellation are still largely manual and less clear.
        *   **Advantages of Structured Concurrency:**
            *   **Better Readability:** The code looks sequential and is easier to reason about because the entire operation is confined to one lexical scope.
            *   **Robust Cancellation:** Cancellation is automatic and propagates cleanly throughout the task hierarchy (e.g., `ShutdownOnFailure`).
            *   **Observability:** The clear structure and thread ownership make it easier to debug and profile concurrent code using standard tools. Thread dumps are more readable because they show the clear parent-child relationship of the tasks.
