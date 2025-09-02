# 18. Decision Matrix - "Ye Problem ki, Ye Tool Vaadali?" 🛠️

Mawa, congratulations! We have covered a massive number of concurrency tools and concepts. Ippudu, the most important question is: "When should I use which tool?"

Ee chapter oka quick reference guide laantidi. It's a summary to help you make the right design decisions. There is no new code in this chapter, only guidance.

## The Concurrency Decision Matrix

Here is a table that maps common problems to the best tool for the job.

| Problem / Scenario | Tool(s) of Choice | Why? (Key Reason) | Common Pitfall / Remember This! |
| :--- | :--- | :--- | :--- |
| **I have a simple, one-off background task.** | `new Thread(runnable).start()` | Simple, direct, no overhead of creating a pool. | Only for very few, non-critical tasks. Never in a loop! |
| **My task needs to return a result.** | `Callable`, `Future`, `ExecutorService` | `Callable` is designed to return a value, and `Future` holds that value. | `future.get()` is blocking! The best way to handle this is with `CompletableFuture`. |
| **I have many tasks and want to control resource usage.** | `ExecutorService` (specifically `ThreadPoolExecutor`) | Reuses threads, limits concurrency, prevents `OutOfMemoryError`. | Avoid `Executors` factory in production. Construct `ThreadPoolExecutor` manually. |
| **I have a huge list of data to process with my CPU.** | `parallelStream()` | The easiest way to get parallelism for CPU-bound data processing. | **DO NOT** use for I/O-bound tasks. It will starve the common `ForkJoinPool`. |
| **I need to run a task every 5 minutes.** | `ScheduledThreadPoolExecutor` | Designed specifically for delayed and periodic task execution. | Remember the difference between `scheduleAtFixedRate` and `scheduleWithFixedDelay`. |
| **I need to protect a complex operation (e.g., check-then-act) from a race condition.** | `synchronized` or `ReentrantLock` | Provides mutual exclusion, ensuring only one thread can be in the critical section at a time. | Can be slow under high contention. Be careful to avoid deadlocks by ordering locks. |
| **I need to safely increment a shared counter with high performance.** | `AtomicInteger` | Uses non-blocking, lock-free CAS operations, which are much faster than `synchronized`. | Only works for single-variable atomic operations. Can't coordinate multiple variables. |
| **I need to make sure a status flag (e.g., `pleaseStop`) is visible to all threads.** | `volatile` | Guarantees visibility of a single variable across threads with low overhead. | Only guarantees visibility, **not atomicity**. Not enough for `count++`. |
| **I need to handle 1,000s of concurrent, blocking I/O requests.** | `Virtual Threads` (`newVirtualThreadPerTaskExecutor`) | Allows massive scalability for blocking code by unmounting from the carrier thread. | Does not speed up CPU-bound work. Watch out for "pinning" with `synchronized`. |
| **I need to manage a complex async workflow with many dependent steps.** | `CompletableFuture` | Provides a powerful, declarative API for composing non-blocking asynchronous tasks. | Can be complex to learn. By default, it uses the common pool; provide a virtual thread executor for blocking tasks. |
| **I need to ensure a group of forked tasks all terminate together (no thread leaks).** | `StructuredTaskScope` (Preview) | Guarantees that the lifetime of sub-tasks is bound to a lexical scope. Simplifies error handling. | It's a preview feature. The API might change. |
| **I need to share a non-thread-safe object (like `SimpleDateFormat`) among many threads.** | `ThreadLocal` | Gives each thread its own private copy of the object, avoiding sharing and synchronization completely. | You **MUST** call `.remove()` in a `finally` block in server environments to prevent memory leaks. |

## What's Next?

Mawa, ee table ni oka cheat sheet la vaaduko. It will help you in system design interviews and in your day-to-day work.

Next, we will move from specific tools to high-level thinking. What are the general rules and guidelines for designing large-scale concurrent systems? That's the topic of our next chapter: **`19_Architecture_Guidelines`**. See you there! 🚀
