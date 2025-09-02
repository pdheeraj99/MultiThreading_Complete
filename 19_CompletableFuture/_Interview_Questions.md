### Interview Questions for Module 19: CompletableFuture

#### Core Concepts

1.  **Question:** What were the main limitations of the original `java.util.concurrent.Future` that `CompletableFuture` was designed to solve?
    *   **Answer:** The original `Future` had three major limitations:
        1.  **Blocking:** There was no way to get the result without blocking the current thread via `future.get()`.
        2.  **No Composability:** You couldn't chain asynchronous operations. It was impossible to say "when this future completes, then do this next thing" without more blocking `get()` calls.
        3.  **Manual Completion:** You couldn't create a `Future` and complete it programmatically with a value later. It could only be completed by the `Executor` that started it.

2.  **Question:** What is the difference between `thenApply`, `thenAccept`, and `thenRun`?
    *   **Answer:** They are all callback methods used to chain operations, differing only in their input and output:
        *   `thenApply(Function<T, U>)`: Takes the result of the previous stage (`T`), **transforms it**, and returns a new result (`U`) for the next stage. It's an intermediate mapping step.
        *   `thenAccept(Consumer<T>)`: Takes the result of the previous stage (`T`) and **consumes it** (e.g., prints it, saves it to a database). It returns `CompletableFuture<Void>`, meaning it doesn't pass a result downstream. It's often a terminal operation in a chain.
        *   `thenRun(Runnable)`: Takes no input and returns no output. It simply executes a `Runnable` action when the previous stage completes. Useful for simple "done" notifications.

3.  **Question:** How do you handle exceptions in a `CompletableFuture` chain?
    *   **Answer:** You use the `exceptionally(Function<Throwable, T>)` method. It acts like a `catch` block for the entire preceding chain. If any stage in the chain throws an exception, all subsequent stages are skipped, and execution jumps directly to the `exceptionally` block. This method receives the `Throwable` and must return a default value of the same type as the future, allowing the chain to recover gracefully. You can also use `handle(BiFunction<T, Throwable, U>)`, which is more like a `finally` block, as it's always executed, receiving either the result or the exception.

#### Scenarios and API

4.  **Question:** What is the difference between `thenApply` and `thenCompose`? This is a very common interview question.
    *   **Answer:**
        *   `thenApply` is for simple, synchronous transformations. You use it when your transformation function returns a plain object (`Function<T, U>`). It wraps that result in a `CompletableFuture` for you.
        *   `thenCompose` is for chaining dependent, asynchronous operations. You use it when your transformation function itself returns a `CompletableFuture` (`Function<T, CompletableFuture<U>>`). `thenCompose` will "flatten" the result, preventing you from ending up with a nested `CompletableFuture<CompletableFuture<U>>`. It's the asynchronous equivalent of a `flatMap` operation.

    *   **Example:** Use `thenApply` to turn a `User` object into a `String` name. Use `thenCompose` to take a `userId` and call another service that returns a `CompletableFuture<User>`.

5.  **Question:** You need to call two independent remote services and combine their results. Which `CompletableFuture` method would you use?
    *   **Answer:** You would use `thenCombine(otherFuture, BiFunction)`. You start both asynchronous calls, which gives you two `CompletableFuture` objects (`futureA` and `futureB`). Then you call `futureA.thenCombine(futureB, (resultA, resultB) -> ...)` to provide a function that will be executed only when both futures have completed successfully, combining their results into a new object.

6.  **Question:** Why is it a best practice to provide your own `Executor` when creating an I/O-bound `CompletableFuture` with `supplyAsync`? What happens if you don't?
    *   **Answer:** If you don't provide an `Executor`, `supplyAsync` will run the task on the default `ForkJoinPool.commonPool()`. This pool is a limited resource, sized to the number of CPU cores, and is intended for CPU-bound tasks. If you run a blocking I/O operation on it (like a network call or database query), the thread will block, consuming one of the few available common pool threads. If many such tasks are submitted, you can exhaust all the threads in the common pool, a situation known as "thread starvation." This can degrade the performance of all other parts of the application that rely on the common pool (like parallel streams). By providing a dedicated `Executor` (e.g., a `Executors.newFixedThreadPool(50)`) for I/O tasks, you isolate the blocking work and prevent it from interfering with CPU-bound work.

#### Comparison

7.  **Question:** How does `CompletableFuture.anyOf()` differ from `CompletableFuture.allOf()`?
    *   **Answer:**
        *   `allOf(cfs...)`: Creates a `CompletableFuture<Void>` that completes only when **all** of the provided futures have completed. It's used when you need to wait for a group of tasks to finish before proceeding.
        *   `anyOf(cfs...)`: Creates a `CompletableFuture<Object>` that completes as soon as **any one** of the provided futures completes. It's used for "racing" scenarios, where you might query multiple redundant services and only care about the first one that responds.
