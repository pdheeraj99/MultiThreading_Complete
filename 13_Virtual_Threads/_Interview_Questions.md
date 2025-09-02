### Interview Questions for Module 13: Virtual Threads (Project Loom)

#### Core Concepts

1.  **Question:** What was the primary motivation behind introducing Virtual Threads in Java? What problem do they solve that Platform Threads couldn't handle efficiently?
    *   **Answer:** The primary motivation was to address the inefficiency of the traditional "thread-per-request" model for I/O-bound tasks. Platform threads are heavyweight, OS-level threads, and creating thousands of them is resource-intensive and leads to poor performance. Virtual Threads are lightweight, managed by the JVM, and allow for a massive number of concurrent tasks without the high overhead, making it easy to write high-throughput concurrent applications.

2.  **Question:** Explain the difference between a Platform Thread and a Virtual Thread. How does the JVM manage Virtual Threads?
    *   **Answer:** A Platform Thread is a thin wrapper around an OS thread (1:1 mapping). It's a scarce and heavyweight resource. A Virtual Thread is a lightweight, user-space thread managed by the JVM, not the OS. The JVM maps a large number of virtual threads onto a small pool of OS threads (carrier threads), typically using a ForkJoinPool. When a virtual thread blocks on I/O, the JVM unmounts it from its carrier thread and mounts another runnable virtual thread, thus keeping the OS thread utilized.

3.  **Question:** What is a "carrier thread"? What happens when a virtual thread performs a blocking I/O operation?
    *   **Answer:** A carrier thread is a platform thread that executes the code of a virtual thread. When a virtual thread encounters a blocking I/O operation (like reading from a socket), the JVM automatically "unmounts" the virtual thread from its carrier thread. The carrier thread is then free to execute another virtual thread. Once the I/O operation is complete, the virtual thread becomes eligible to be "mounted" back onto any available carrier thread to continue its execution. This is the key to their efficiency.

#### Deeper Dive & Scenarios

4.  **Question:** When would you still prefer to use Platform Threads over Virtual Threads? Give a specific example.
    *   **Answer:** You should prefer Platform Threads for CPU-intensive, long-running tasks that do not involve blocking I/O. Since these tasks keep the CPU busy, they would monopolize the carrier thread anyway, and the benefits of virtual threads (unmounting on blocking) wouldn't apply. For example, a task that performs complex mathematical calculations, video encoding, or heavy data processing without I/O is better suited for a platform thread.

5.  **Question:** What is "thread pinning" in the context of Virtual Threads? Why is it a problem, and how can it be mitigated?
    *   **Answer:** Thread pinning occurs when a virtual thread is "stuck" or "pinned" to its carrier thread, preventing the carrier from being released even if the virtual thread is blocked. This happens when executing `synchronized` blocks or native methods (JNI). If a virtual thread blocks inside a synchronized block, the carrier thread is also blocked, which undermines the scalability of virtual threads. To mitigate this, the recommendation is to replace `synchronized` blocks with `java.util.concurrent.locks.ReentrantLock`, which is "virtual-thread-aware" and will not cause pinning.

6.  **Question:** How does the introduction of Virtual Threads impact the use of `ThreadLocal`? What are the potential issues?
    *   **Answer:** `ThreadLocal` was designed with the assumption that threads are scarce. With virtual threads, you can have millions of them. If each of the millions of virtual threads uses a `ThreadLocal`, it can lead to massive memory consumption, as the `ThreadLocal` map will hold a reference for every thread. This can lead to `OutOfMemoryError`. While `ThreadLocal` works with virtual threads, its use is heavily discouraged. Scoped Values are the intended replacement for `ThreadLocal` in the structured concurrency model.

#### Code & API

7.  **Question:** How do you create a virtual thread? Show two different ways using the modern Java API.
    *   **Answer:**
        1.  Using `Thread.startVirtualThread(Runnable)`: `Thread.startVirtualThread(() -> System.out.println("Hello"));`
        2.  Using a `VirtualThreadPerTaskExecutor`:
            ```java
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                executor.submit(() -> System.out.println("Hello from executor"));
            }
            ```

8.  **Question:** You have a legacy application that uses `Executors.newCachedThreadPool()` to handle I/O-bound tasks. How would you migrate this to use virtual threads for better performance, and what would be the main benefit?
    *   **Answer:** You would replace `Executors.newCachedThreadPool()` with `Executors.newVirtualThreadPerTaskExecutor()`. The change is minimal.
        *   **Before:** `ExecutorService executor = Executors.newCachedThreadPool();`
        *   **After:** `ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();`
        The main benefit is scalability. A cached thread pool creates new platform threads as needed, which can quickly lead to resource exhaustion (e.g., thousands of threads). The virtual thread executor can handle millions of tasks with a small, fixed number of platform threads, preventing `OutOfMemoryError` and improving throughput for I/O-bound workloads.
