# 19. Architecture Guidelines - The 7 Commandments of Concurrent Design 🙏

Mawa, welcome to Chapter 19. Ippativaraku manam chala tools and techniques nerchukunnam. But a good software architect doesn't just know the tools; they know the **principles** behind them.

This chapter contains the high-level guidelines or "commandments" for designing clean, robust, and maintainable concurrent systems.

---

## The 7 Commandments of Concurrent Architecture

### 1. Thou Shalt Minimize Shared Mutable State (The Golden Rule)
This is the most important rule. Concurrency problems (race conditions, deadlocks, visibility) only happen when multiple threads are trying to access and **change the same data**.
*   **The Rule:** If you can design your code so that threads don't share changing data, you will have no concurrency problems. It's that simple.
*   **How:**
    *   **Immutability:** Make your objects immutable. An immutable object's state cannot be changed after it's created (like a `String`). Immutable objects are inherently thread-safe.
    *   **Isolation:** Give each thread its own copy of the data. This is what `ThreadLocal` does.
    *   **Message Passing:** Instead of sharing memory, have threads communicate by passing messages to each other (like in the Actor model).

### 2. Thou Shalt Prefer High-Level Abstractions
Don't reinvent the wheel, especially a complex, wobbly, concurrent wheel.
*   **The Rule:** Always use the highest-level concurrency utility that solves your problem.
*   **The Hierarchy:**
    *   Don't use `wait()`/`notify()` if a `BlockingQueue` will do.
    *   Don't use `synchronized` for a simple counter if `AtomicInteger` will do.
    *   Don't manage threads manually (`new Thread()`) if an `ExecutorService` will do.
    *   Don't build complex async callbacks if `CompletableFuture` will do.
*   **Why:** The classes in `java.util.concurrent` were written and reviewed by world-class experts (like Doug Lea). They are more efficient, less error-prone, and more readable than anything you could write yourself.

### 3. Thou Shalt Keep Critical Sections Small
When you absolutely must use locking (`synchronized` or `ReentrantLock`), your goal should be to hold the lock for the shortest possible time.
*   **The Rule:** Do not include long-running operations like I/O calls or complex computations inside a `synchronized` block.
*   **Why:** The longer you hold a lock, the longer other threads have to wait (contention). This kills performance and scalability.
*   **How:** Prepare all data *before* entering the `synchronized` block. Enter the block, do the quick state change, and exit immediately.

### 4. Thou Shalt Isolate Blocking Operations
This is a key lesson from our `parallelStream` and `CompletableFuture` chapters.
*   **The Rule:** Never run blocking I/O tasks on a shared, CPU-bound thread pool (like the common `ForkJoinPool`).
*   **Why:** It leads to thread pool starvation, which can cripple your entire application.
*   **How:** Create a separate, dedicated `ExecutorService` for your blocking tasks. In modern Java, the best choice for this is `Executors.newVirtualThreadPerTaskExecutor()`.

### 5. Thou Shalt Favor Composition over Inheritance
We learned this in Chapter 2, but it's a core design principle.
*   **The Rule:** `implements Runnable` is almost always better than `extends Thread`.
*   **Why:** It separates the "task" from the "runner," which is a cleaner design. It also doesn't use up your single "extends" slot, making your code more flexible.

### 6. Thou Shalt Think About Shutdown
A concurrent task that never stops is a resource leak.
*   **The Rule:** Always have a clear plan for how your threads and thread pools will be shut down.
*   **How:**
    *   Use `ExecutorService.shutdown()` and `awaitTermination()`.
    *   For tasks in loops, use a `volatile boolean` flag or `interrupts` to signal cancellation.
    *   For producer-consumer, use a "poison pill."
    *   For complex workflows, use `StructuredTaskScope` to guarantee cleanup.

### 7. Thou Shalt Choose the Right Tool for the Job
Don't use a tool just because it's new or powerful. Understand the trade-offs.
*   **The Rule:** Use the Decision Matrix from Chapter 18.
*   **Examples:**
    *   Is it a simple visibility problem? Use `volatile`.
    *   Is it a simple atomic counter? Use `AtomicInteger`.
    *   Is it a complex atomic operation? Use `synchronized` or `ReentrantLock`.
    *   Is it a "divide and conquer" algorithm? Use `ForkJoinPool`.
    *   Is it a scalable I/O-bound web service? Use `Virtual Threads`.

## What's Next?

Mawa, if you internalize these 7 commandments, you will be designing concurrent systems like a true 30lpa architect.

Next, we'll look at a very practical topic: what can go wrong with Virtual Threads and how to troubleshoot them? See you in **`20_Virtual_Threads_Troubleshooting`**. 🚀
