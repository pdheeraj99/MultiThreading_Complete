# 12. Parallel & Reactive Streams - Data Processing on Steroids ⚡

Mawa, welcome to Chapter 12! So far, manam tasks ni concurrently run cheyadam chusam. Ippudu manam focus ni data processing meeda pedadam. What if you have a huge amount of data and you want to process it faster?

This chapter is a supportive one. It introduces two powerful data processing concepts.

---

## Part 1: Parallel Streams

Let's say you have a large list of numbers and you want to perform a CPU-intensive operation on each one.

### The Problem: Sequential Processing is Slow

With standard Java Streams, the processing happens sequentially, on a single thread. This is simple, but if you have 8 CPU cores, only one is doing the work. The other 7 are sitting idle. What a waste!

### The Solution: `.parallelStream()`

**The "What they thought" story:** The Java 8 creators wanted to provide a very easy way for developers to take advantage of multi-core processors for data processing without learning complex frameworks.

**The "What happened next" story:** They created `parallelStream()`. With a single method call change, it automatically uses the **`ForkJoinPool`** (which we'll learn about in Chapter 16) to split the work across multiple threads. It was a huge success for CPU-bound tasks but led to a new problem when misused.

**The Analogy: Multiple Assembly Lines**
*   **`.stream()`:** You have one long assembly line.
*   **`.parallelStream()`:** You have multiple assembly lines. The work is divided among them.

#### The New Problem: The Blocking I/O Trap
Developers loved `parallelStream()` so much they started using it for everything, including I/O-bound tasks like making network calls for each item in the stream. This was a disaster. It would quickly block all the threads in the shared, common `ForkJoinPool`, starving the entire application.

**Rule:** Parallel streams are for **CPU-bound** work, not I/O-bound work.

---

## Part 2: Reactive Streams

Parallel streams are about processing a *finite* set of data faster. But what if your data is an *infinite* stream that arrives over time?

### The Problem: The "Pull" Model is Inefficient

Traditionally, we "pull" data (`iterator.next()`). This is inefficient for data that arrives asynchronously.

### The Solution: The "Push" Model (Reactive Programming)

**The "What they thought" story:** A group of engineers (including from Netflix and Pivotal) saw the need for a standard, non-blocking, asynchronous way to handle streams of data with backpressure.

**The "What happened next" story:** They created the Reactive Streams specification. This became so popular that it was officially adopted into Java 9 as the `java.util.concurrent.Flow` API.

**The Analogy: Netflix vs. YouTube Notifications**
*   **Pull Model:** You go to Netflix every hour to check for a new episode.
*   **Push Model (Reactive):** You **subscribe** to a YouTube channel. When a new video is uploaded, YouTube **pushes** a notification to you.

The core idea is that the **Publisher** pushes data to the **Subscriber**. The Subscriber can apply **backpressure**, telling the Publisher to slow down if it's overwhelmed.

**Note:** The `Flow` API just provides the interfaces. For real-world applications, developers use full-fledged reactive libraries like **Project Reactor** or **RxJava**.

## What's Next?
Now, let's get back to our main track and solve the "blocking I/O trap" we've seen in both `CompletableFuture` and `parallelStream`. The solution is a revolutionary feature: **Virtual Threads**. See you in Chapter 13! 🚀
