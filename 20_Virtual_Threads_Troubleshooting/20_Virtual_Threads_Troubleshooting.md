# 20. Virtual Threads Troubleshooting - The Common Pitfalls 🕵️‍♂️

Mawa, welcome to Chapter 20! Virtual threads are powerful, but they are not a magic wand. There are a few common traps that developers can fall into. Understanding these pitfalls is key to using virtual threads effectively.

This chapter is a practical guide to troubleshooting.

---

## The #1 Problem: Thread Pinning

This is the most common and important issue to understand when working with virtual threads.

**The Symptom:** My application uses virtual threads, but it's not scaling as much as I expected. Under high load, it seems to slow down, and the throughput is poor.

**The Cause:** A virtual thread becomes **"pinned"** to its carrier (the OS platform thread) when it enters a `synchronized` block or method and then attempts a blocking operation (like I/O) *inside* that synchronized block.

When a virtual thread is pinned, the JVM **cannot unmount it**. The carrier OS thread is now stuck waiting for the blocking operation to complete, just like in the old platform thread model. If you have many threads getting pinned, you will exhaust the small number of carrier threads, and your application loses its scalability advantage.

**The Primary Culprit: `synchronized`**
The `synchronized` keyword is the main cause of pinning.

```java
// DANGER: This will pin the carrier thread!
public synchronized void blockingOperation() {
    // 1. The virtual thread enters this method and acquires the intrinsic lock.
    // 2. It is now PINNED to the carrier thread.

    // 3. It now makes a blocking call.
    socket.read(); // The carrier thread is now blocked! It cannot be used by other virtual threads.
}
```

### How to Find Pinning?

The JDK provides a simple way to diagnose pinning. Just run your application with this system property:
`-Djdk.tracePinnedThreads=full`

When your application runs, if a thread is pinned for a significant amount of time, the JVM will print a detailed stack trace to the console, showing you exactly which `synchronized` block is causing the problem.

### How to Fix Pinning?

The solution is to **avoid `synchronized` and use `java.util.concurrent.locks.ReentrantLock` instead.**

The `ReentrantLock` class has been updated to be aware of virtual threads. When a virtual thread acquires a `ReentrantLock` and then blocks, it does **not** pin the carrier thread. The JVM can safely unmount the virtual thread.

```java
// SAFE: This will NOT pin the carrier thread.
private final ReentrantLock lock = new ReentrantLock();

public void blockingOperation() {
    lock.lock(); // 1. Acquire the ReentrantLock
    try {
        // 2. Make the blocking call. The virtual thread will be unmounted here.
        socket.read();
    } finally {
        lock.unlock(); // 3. Release the lock
    }
}
```
**The Rule:** In the era of virtual threads, prefer `ReentrantLock` over the `synchronized` keyword for locking.

---

## Other Common Issues

### Issue 2: Using Virtual Threads for CPU-Bound Work

**The Symptom:** My CPU-intensive algorithm is not running any faster with virtual threads.

**The Cause:** Virtual threads are designed to improve scalability for **I/O-bound** tasks by not blocking the carrier thread during waits. They do not make the CPU itself faster. If you have 8 CPU cores, you can only run 8 truly parallel CPU-bound tasks, regardless of whether you use platform or virtual threads.

**The Fix:** Stick to a `FixedThreadPool` of platform threads for CPU-bound work. A pool size equal to the number of CPU cores is usually optimal.

### Issue 3: Using Old, Bounded Thread Pools

**The Symptom:** I'm submitting tasks that use virtual threads, but only a few seem to run at a time.

**The Cause:** You might be submitting your virtual thread tasks to an old, fixed-size platform thread pool (like `Executors.newFixedThreadPool(10)`). This completely defeats the purpose of virtual threads, as you are still limited by the size of the platform thread pool.

**The Fix:** Always use `Executors.newVirtualThreadPerTaskExecutor()` when you want to run tasks on virtual threads. This executor creates a new virtual thread for every task, which is cheap and is the intended way to use them.

## What's Next?

Mawa, with this knowledge, you are now a pro at not just using virtual threads, but also at troubleshooting them.

We are now nearing the end of our core learning. The next few chapters will focus on high-level integration, performance tuning, and putting everything together. Let's start with **`21_Performance_and_Profiling`**. See you there! 🚀
