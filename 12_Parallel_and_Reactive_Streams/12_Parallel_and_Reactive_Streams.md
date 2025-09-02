# 12. Parallel & Reactive Streams - Data Processing on Steroids ⚡

Mawa, welcome to Chapter 12! So far, manam tasks ni concurrently run cheyadam chusam. Ippudu manam focus ni data processing meeda pedadam. What if you have a huge amount of data and you want to process it faster?

This chapter is a supportive one. It introduces two powerful data processing concepts.

---

## Part 1: Parallel Streams

Let's say you have a large list of numbers and you want to perform a CPU-intensive operation on each one.

### The Problem: Sequential Processing is Slow

With standard Java Streams, the processing happens sequentially, on a single thread.
```java
List<Integer> numbers = ... // a list with 1,000,000 numbers
List<Integer> results = numbers.stream()
                               .map(n -> performComplexCalculation(n))
                               .collect(Collectors.toList());
```
If you have 8 CPU cores, only one is doing the work. The other 7 are sitting idle. What a waste! How can we easily use all our cores to speed this up?

### The Solution: `.parallelStream()`

Java 8 introduced a stunningly simple solution: **parallel streams**.
```java
List<Integer> results = numbers.parallelStream() // That's it!
                               .map(n -> performComplexCalculation(n))
                               .collect(Collectors.toList());
```
By just changing `.stream()` to `.parallelStream()`, you are telling Java: "Hey, take this huge collection, split it into smaller chunks, and process those chunks in parallel on multiple threads/cores."

**The Analogy: Multiple Assembly Lines**
*   **`.stream()`:** You have one long assembly line. Every item goes through it one by one.
*   **`.parallelStream()`:** You have multiple assembly lines. The work is divided among them, and they all work at the same time. The total time to process everything is drastically reduced.

Under the hood, parallel streams use the **`ForkJoinPool`** (which we will cover in detail in Chapter 16), the same common pool used by `CompletableFuture` by default.

#### Advantages and Disadvantages of Parallel Streams

**Advantages 👍:**
1.  **Simple:** An incredibly easy way to achieve parallelism for data processing tasks.
2.  **Performance Boost:** Can provide a massive performance improvement for CPU-intensive operations on large datasets.

**Disadvantages 👎:**
1.  **Not a Magic Bullet:** It's not always faster. For small collections, the overhead of splitting the work and managing threads can actually make it *slower* than a sequential stream.
2.  **Blocking I/O is a Killer:** Just like with `CompletableFuture`, if the operation inside your `.map()` or `.filter()` is a blocking I/O call (like a network request), you will starve the common `ForkJoinPool` and bring your entire application to a crawl. **Parallel streams are for CPU-bound work, not I/O-bound work.**
3.  **Stateful Operations:** If your lambda expressions are stateful (i.e., they modify a shared variable), using a parallel stream can lead to race conditions.

---

## Part 2: Reactive Streams (A Quick Introduction)

Parallel streams are about processing a *finite* set of data faster. But what if your data is an *infinite* stream that arrives over time? Like a stream of stock market ticks, or user clicks on a website.

### The Problem: The "Pull" Model is Inefficient

Traditionally, we "pull" data.
`iterator.next()` - "Give me the next item."
`future.get()` - "Give me the result now."
This is inefficient for data that arrives asynchronously. You would have to constantly poll for new data.

### The Solution: The "Push" Model (Reactive Programming)

Reactive programming flips the model. You don't ask for data; the data is **pushed** to you when it's ready.

**The Analogy: Netflix vs. YouTube Notifications**
*   **Pull Model (Iterator/`get()`):** You go to the Netflix homepage every hour to check if your favorite show has a new episode. You are polling.
*   **Push Model (Reactive):** You **subscribe** to a YouTube channel. When the creator uploads a new video, YouTube **pushes** a notification to you. You simply **react** to the event.

Java 9 introduced the `java.util.concurrent.Flow` API, which defines the standard interfaces for reactive programming in Java.

#### The 4 Core Interfaces of `Flow`

1.  **`Publisher<T>`**: The data source (the YouTube channel). It has a `subscribe()` method.
2.  **`Subscriber<T>`**: The consumer (you). It has four methods to react to events:
    *   `onSubscribe(Subscription s)`: Called once at the beginning. You are now subscribed.
    *   `onNext(T item)`: Called every time a new data item is pushed.
    *   `onError(Throwable t)`: Called if an error occurs.
    *   `onComplete()`: Called when the publisher has no more data.
3.  **`Subscription`**: Represents the connection between a Publisher and a Subscriber. The subscriber uses this to request data (`subscription.request(n)`) or cancel the subscription.
4.  **`Processor<T, R>`**: A stage that is both a Subscriber (it receives data) and a Publisher (it transforms the data and sends it to other subscribers).

A key concept in reactive streams is **backpressure**. The Subscriber can tell the Publisher, "Hey, slow down! I can only handle 10 items right now" by using `subscription.request(10)`. This prevents the consumer from being overwhelmed.

**Note:** The `Flow` API just provides the interfaces. For real-world applications, developers use full-fledged reactive libraries like **Project Reactor** (used by Spring WebFlux) or **RxJava**, which provide rich sets of operators to work with these streams.

## What's Next?

This was a high-level overview of two powerful data processing paradigms. Now, let's get back to our main track and tackle the problem of blocking I/O that we've seen in both `CompletableFuture` and `parallelStream`. The solution is a revolutionary feature in modern Java: **Virtual Threads**. See you in Chapter 13! 🚀
