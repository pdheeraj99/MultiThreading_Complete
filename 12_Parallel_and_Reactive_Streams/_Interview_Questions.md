# 💬 Interview Questions & Answers - Topic 12: Parallel & Reactive Streams

Mawa, ee topics nunchi questions nee breadth of knowledge ni test chestayi. Parallel streams gurinchi adigithe, nuvvu daani limitations gurinchi cheppali. Reactive gurinchi adigithe, adi oka programming model shift ani cheppali.

---

### Scenario 1: The Parallel Stream Trap

**Interviewer:** "I have a list of 100 user IDs. I need to make a network call for each user to get their profile. To speed it up, I used a parallel stream. Is this a good idea? Why or why not?"

```java
List<UserProfile> profiles = userIds.parallelStream()
                                    .map(id -> makeNetworkCallToGetUserProfile(id))
                                    .collect(Collectors.toList());
```

**Why this question?**
This is the most important question about parallel streams. It tests if you know their biggest pitfall and when *not* to use them.

**How to Answer:**

"This is generally a **bad idea**, and it could actually make the application's performance much worse.

**The Reason:** Parallel streams, by default, run on the common `ForkJoinPool`. This pool has a limited number of threads, typically equal to the number of CPU cores. The `ForkJoinPool` is designed for **CPU-bound** tasks—tasks that perform intensive calculations and keep the CPU busy.

A network call is an **I/O-bound** task. The thread that makes the network call will spend most of its time **blocked**, waiting for a response from the network.

**The Problem (Pool Starvation):**
If you submit 100 of these blocking tasks to the common pool (which might only have 8 threads), all 8 threads will quickly become blocked waiting for network responses. The `ForkJoinPool` is now completely starved of threads. This is a huge problem because other parts of the application that rely on the common pool (like `CompletableFuture` tasks) will also be unable to run. You've created a system-wide bottleneck.

**The Solution:**
Parallel streams should not be used for I/O-bound work. The correct tool for this kind of I/O parallelism is `CompletableFuture`, where you can provide a separate, dedicated `ExecutorService` designed to handle I/O tasks. This isolates the blocking work and doesn't impact the rest of the application."

---

### Scenario 2: The Core of "Reactive"

**Interviewer:** "What is the fundamental difference between the 'pull model' of an `Iterator` and the 'push model' of a Reactive Stream? What kind of problem does the 'push model' solve?"

**Why this question?**
This question tests if you understand the conceptual shift behind reactive programming. It's not about a specific library, but about the core idea.

**How to Answer:**

"The difference lies in who controls the flow of data.

**1. Pull Model (`Iterator`):**
   - In the pull model, the **Consumer** is in control.
   - The consumer explicitly asks for data by calling `iterator.next()`. If there's no data, the consumer might have to wait or poll.
   - **Analogy:** You go to a website and repeatedly hit "refresh" to see if there's new content. You are "pulling" the data.
   - This model is fine for finite, readily available data sources like a `List`.

**2. Push Model (Reactive Stream):**
   - In the push model, the **Publisher** is in control.
   - The Consumer (called a `Subscriber`) expresses interest once by subscribing. After that, the Publisher **pushes** data to the Subscriber whenever new data becomes available. The Subscriber just reacts to the `onNext()` event.
   - **Analogy:** You subscribe to a YouTube channel. When a new video is released, the platform "pushes" a notification to you. You don't have to check for it.

**The Problem Solved by the Push Model:**
The push model is designed to solve the problem of handling **asynchronous streams of data that arrive over time**. This is very common in modern applications:
*   Streams of user UI events (mouse clicks, key presses).
*   Real-time data feeds from a server (stock ticks, social media updates).
*   Handling responses from non-blocking I/O operations.

The reactive model, with its concept of **backpressure** (where the subscriber can signal to the publisher to slow down), provides an efficient, non-blocking way to handle these asynchronous data streams without overwhelming the consumer."

**Pro Tip 💡:**
Using the "pull vs. push" terminology and a simple analogy like "refreshing a website vs. getting a notification" makes the concept very easy to understand. Mentioning "backpressure" is a key indicator that you understand the nuances of reactive systems.
