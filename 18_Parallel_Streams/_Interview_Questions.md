### Interview Questions for Module 18: Parallel Streams

#### Core Concepts

1.  **Question:** How do parallel streams work "under the hood"? What concurrency utility powers them?
    *   **Answer:** Parallel streams are a high-level abstraction built on top of the **Fork/Join Framework**. When you call `.parallelStream()`, the collection is broken into chunks by a `Spliterator`. These chunks are then wrapped in `ForkJoinTask`s and submitted for processing to the **common `ForkJoinPool`** (`ForkJoinPool.commonPool()`). The pool's work-stealing mechanism is used to efficiently process the data across multiple cores.

2.  **Question:** What is the key difference between a sequential stream (`.stream()`) and a parallel stream (`.parallelStream()`)?
    *   **Answer:** A sequential stream processes the elements of the collection one by one on a single thread. A parallel stream partitions the source data and processes the partitions concurrently on multiple threads, leveraging the common `ForkJoinPool`. The primary benefit is a potential performance increase for CPU-intensive operations on large datasets.

3.  **Question:** What is a "stateful lambda," and why is it dangerous to use one in a parallel stream?
    *   **Answer:** A stateful lambda is a lambda expression that has side-effects, meaning it modifies some shared state that exists outside of the lambda itself (e.g., adding an element to a list defined outside the stream). This is dangerous in a parallel stream because multiple threads will execute the lambda concurrently, leading to a race condition on the shared state. If the shared state is not thread-safe (like an `ArrayList`), this will cause data corruption, incorrect results, and/or exceptions.

#### Scenarios and Best Practices

4.  **Question:** Give three scenarios where you should *avoid* using a parallel stream.
    *   **Answer:**
        1.  **Small Datasets:** The overhead of partitioning the data and managing threads can be greater than the performance gain. For small collections, a sequential stream is often faster.
        2.  **I/O-Bound Operations:** If the stream operations involve blocking I/O (like network requests or file access), this will block the threads in the common `ForkJoinPool`. This defeats the purpose of parallelism and can starve other parts of the application that rely on the common pool.
        3.  **When Order is Critical:** Operations like `findFirst()` are non-deterministic in a parallel stream. If the logic depends on a strict processing order, a sequential stream must be used. For terminal operations that must preserve order, `forEachOrdered()` can be used, but it may sacrifice some parallelism.

5.  **Question:** You have a parallel stream, and you need to collect the results into a `List`. What is the correct way to do this, and why is `forEach(list::add)` wrong?
    *   **Answer:** The correct way is to use a `Collector`: `.collect(Collectors.toList())`.
    *   `forEach(list::add)` is wrong because it's a stateful operation. `list::add` is a lambda with a side-effect: modifying the shared `list`. When run in parallel, multiple threads will call `add()` on the non-thread-safe list concurrently, causing a race condition and corrupting the list. The `collect` operation is designed to be performed in parallel safely. It works by having each thread accumulate results into its own private, intermediate container, and then all the intermediate containers are merged together safely at the end.

6.  **Question:** Does calling `.parallel()` on a stream guarantee that it will run faster?
    *   **Answer:** No, it does not. It's a common misconception. A performance improvement is possible, but only under the right conditions:
        *   The dataset must be large enough to justify the overhead.
        *   The processing per element must be CPU-intensive.
        *   The machine must have multiple CPU cores.
        *   The stream operations must be stateless and safe to parallelize.
        In many cases (especially with small datasets or simple operations), a parallel stream can actually be slower than its sequential counterpart. The only way to know for sure is to measure.

#### Advanced Concepts

7.  **Question:** Is it possible to make a parallel stream use a custom thread pool instead of the common `ForkJoinPool`?
    *   **Answer:** This is a trick question. You cannot directly tell a parallel stream, "use this specific executor." The parallel stream API is hardwired to use the common `ForkJoinPool`. However, you *can* achieve the same effect by creating your own `ForkJoinPool` and submitting a task to it. Inside that task, you can then create and run your parallel stream. This confines the execution of that specific parallel stream to your custom pool, preventing it from interfering with or being starved by other tasks in the common pool.

    ```java
    // Example of the advanced pattern
    ForkJoinPool myPool = new ForkJoinPool(4);
    myPool.submit(() -> {
        long count = myList.parallelStream().filter(...).count();
        // ...
    }).get();
    ```
