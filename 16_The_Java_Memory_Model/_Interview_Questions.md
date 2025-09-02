### Interview Questions for Module 16: The Java Memory Model (JMM)

#### Core Concepts

1.  **Question:** What is the core problem that the Java Memory Model (JMM) solves?
    *   **Answer:** The JMM solves the problem of unpredictability in multithreaded programs caused by hardware and compiler optimizations. Specifically, it addresses two main issues:
        1.  **Visibility:** Changes made to a shared variable by one thread may not be visible to other threads because the change is stuck in the first thread's local CPU cache.
        2.  **Instruction Reordering:** Compilers and CPUs can reorder instructions to improve performance, which can break the logic of a program that relies on a specific order of operations across multiple threads.
    The JMM provides a formal set of guarantees about when memory writes by one thread are visible to another.

2.  **Question:** What is a "happens-before" relationship in the JMM? Why is it important?
    *   **Answer:** A "happens-before" relationship is the central guarantee of the JMM. If action A *happens-before* action B, then the results of action A are guaranteed to be visible to and ordered before action B. For example, releasing a lock *happens-before* acquiring that same lock. A write to a `volatile` variable *happens-before* any subsequent read of that same `volatile` variable. This is important because it's the mechanism that allows us to reason about the order and visibility of operations across threads.

3.  **Question:** Explain the memory guarantees provided by the `synchronized` keyword.
    *   **Answer:** The `synchronized` keyword provides two guarantees:
        1.  **Mutual Exclusion:** Only one thread can execute a synchronized block on a given monitor object at a time, preventing race conditions in critical sections.
        2.  **Visibility:** When a thread exits a synchronized block, it flushes all of its modified variables to main memory. When a thread enters a synchronized block, it clears its local cache, forcing it to load the latest values from main memory. This establishes a *happens-before* relationship.

#### `volatile` vs. `synchronized`

4.  **Question:** What guarantees does the `volatile` keyword provide, and how does it differ from `synchronized`?
    *   **Answer:** The `volatile` keyword provides two guarantees:
        1.  **Visibility:** A read of a volatile variable will always see the most recent write by any thread.
        2.  **Ordering:** It prevents the compiler/CPU from reordering instructions around the volatile variable read/write.
    *   **Difference:** `volatile` only provides visibility and ordering guarantees; it does **not** provide atomic mutual exclusion (locking). `synchronized` provides locking in addition to visibility. Therefore, `volatile` is a lighter-weight synchronization mechanism suitable for simple flags or status indicators, but not for compound actions.

5.  **Question:** When would you use `volatile` instead of `synchronized`?
    *   **Answer:** You would use `volatile` when you need to ensure visibility of a shared variable, but you do not need to enforce atomic, mutually exclusive access. A typical use case is a simple boolean flag used to signal a status change from one thread to another (e.g., `volatile boolean shutdownRequested`). If multiple threads need to perform a read-modify-write sequence (like `count++`), `volatile` is not sufficient, and you should use `synchronized` or an atomic class.

#### Modern Concurrency

6.  **Question:** Why is `volatile` not sufficient to make `count++` thread-safe?
    *   **Answer:** The operation `count++` is not a single, atomic operation. It is a sequence of three separate operations: a read, a modify, and a write. Declaring `count` as `volatile` ensures that the read and write operations are fresh from main memory, but it does not prevent another thread from reading the same value *after* the first thread has read it but *before* it has written the new value back. This can lead to lost updates.

7.  **Question:** What is the modern, preferred way to handle a shared, mutable counter in a multithreaded environment, and why is it better than using `volatile` or `synchronized`?
    *   **Answer:** The modern, preferred way is to use `java.util.concurrent.atomic.AtomicInteger`.
    *   **Why it's better:**
        *   **vs. `volatile`:** `AtomicInteger` provides atomic methods like `incrementAndGet()` that safely execute the entire read-modify-write sequence as an indivisible unit, preventing the race condition that `volatile` alone cannot.
        *   **vs. `synchronized`:** `AtomicInteger` typically uses non-blocking, hardware-level instructions (like Compare-And-Swap or CAS) under the hood. This is often much more performant and scalable than using `synchronized`, which involves acquiring a lock that can lead to thread contention and suspension. It provides the necessary safety without the heavy overhead of locking. It's a fine-tuned tool for the specific job of atomic updates.
