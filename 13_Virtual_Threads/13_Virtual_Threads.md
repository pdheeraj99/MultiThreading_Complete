# 13. Virtual Threads - The "Million-Request" Revolution 🚀

Mawa, welcome to one of the most exciting chapters in this entire series. This is a game-changer. For years, Java developers have been told, "Threads are expensive. Don't create too many. Use async programming for high scalability." Project Loom and Virtual Threads have turned that advice on its head.

## The Problem: The Scalability Wall

Let's go back to our web server example. The classic, easy-to-understand model is "thread-per-request":
1.  A new client connection comes in.
2.  The server dedicates a thread to handle that request from start to finish.
3.  The thread might make a blocking database call or a network call to another service.
4.  The thread waits for the response, processes it, and sends a reply to the client.

This model is simple, easy to write, and easy to debug. The problem? **It doesn't scale.**

Traditional threads in Java (now called **Platform Threads**) are just thin wrappers around an Operating System (OS) thread. OS threads are heavy. They have large stacks and are managed by the OS scheduler. You can only have a few thousand of them before your server runs out of memory and crashes.

So, if you want to handle 100,000 concurrent users, the thread-per-request model is impossible. This "scalability wall" is what forced developers to move to complex, non-blocking, asynchronous programming models (like `CompletableFuture` or libraries like Netty/Vert.x).

The problem is: **How can we handle massive numbers of concurrent I/O-bound tasks without giving up our simple, easy-to-read, synchronous "thread-per-request" style of code?**

## The Solution: Virtual Threads (Project Loom)

Virtual Threads are the answer. They are extremely **lightweight threads managed by the JVM**, not the OS.

**The Analogy: The Super-Efficient Post Office**
*   **Platform Threads (Old Post Office):** You have 8 service counters (OS threads). When a customer with a long, blocking task (like waiting for a passport photo) comes, they occupy one of the 8 counters for the entire duration. The clerk (OS thread) is stuck waiting with them. You can only serve 8 such customers at a time.
*   **Virtual Threads (New Post Office):** You still have the same 8 service counters (these are now called "carrier" threads). But you also have a million lightweight plastic trays (Virtual Threads).
    1.  A customer comes with a long task. The clerk takes their paperwork and puts it on a tray (mounts the virtual thread).
    2.  The clerk sees the task is a long-waiting one. Instead of waiting, they put the tray in a "waiting area" (**unmounts** the virtual thread) and immediately become free.
    3.  The clerk is now free to pick up another tray and work on another customer's task.
    4.  When the passport photo for the first customer is ready, a notification arrives. **Any free clerk** can pick up that customer's tray (mounts the virtual thread again) and complete the process.

The key idea is that the expensive resource (the OS thread / the clerk) is **never blocked**. When a virtual thread encounters a blocking I/O operation, the JVM automatically unmounts it from the carrier thread and lets the carrier do other work. This allows a small number of platform threads to handle a massive number of virtual threads.

You can create millions of virtual threads without running out of memory.

### How to Create Virtual Threads

The APIs are incredibly simple.

**1. Using `Thread.ofVirtual()`:**
```java
// Create and start a virtual thread
Thread.ofVirtual().start(() -> {
    System.out.println("I'm a virtual thread!");
});
```

**2. Using a new `ExecutorService`:**
This is the most powerful way. It creates a new virtual thread for every single task you submit. You don't have to worry about a pool size.
```java
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> { System.out.println("Task 1"); });
    executor.submit(() -> { System.out.println("Task 2"); });
} // The executor is automatically closed
```

With this, the "thread-per-request" model becomes highly scalable again!

### Advantages and Disadvantages of Virtual Threads

**Advantages 👍:**
1.  **Massive Scalability:** Allows you to handle millions of concurrent I/O-bound tasks with a small number of platform threads.
2.  **Simple Code:** You can write simple, sequential, blocking code that is easy to read and debug, and still get the scalability of asynchronous programming.
3.  **Lightweight:** They have a very small memory footprint compared to platform threads.

**Disadvantages / Things to Know 👎:**
1.  **Not for CPU-Bound Work:** Virtual threads do not make your CPU faster. If you have 8 cores, you can still only run 8 CPU-intensive tasks at a time. Running 100 CPU-bound tasks on 100 virtual threads will not be faster than running them on a fixed pool of 8 platform threads. Virtual threads are for increasing concurrency for **blocking** tasks.
2.  **"Pinning" with `synchronized`:** If a virtual thread enters a `synchronized` block and then performs a blocking operation, the JVM **cannot** unmount it. The carrier thread becomes "pinned" and is blocked. This can degrade performance. This is a reason to prefer `java.util.concurrent.locks.ReentrantLock` over `synchronized` in the era of virtual threads.

## The New Problem: Unstructured Chaos

Virtual threads make it easy to create thousands or millions of threads. But this creates a new problem: how do we manage them? If we fire off 10 tasks for a single operation, and one fails, how do we cancel the other 9? How do we ensure they all shut down properly if the user cancels the operation?

This is the problem of **unstructured concurrency**. To solve it, we need a new model that treats a group of related tasks as a single unit. But first, let's see how virtual threads and `CompletableFuture` work together.

## What's Next?

First, in **`14_CompletableFuture_and_Virtual_Threads`**, we'll see how to combine these two powerful tools. Then, in **`15_Structured_Concurrency`**, we will solve the problem of "unstructured chaos" for good. See you there! 🚀
