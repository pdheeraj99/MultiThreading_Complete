# 💬 Interview Questions & Answers - Topic 14: CompletableFuture & Virtual Threads

Mawa, ee combination anedi bleeding-edge Java. Deeni gurinchi adugutunnaru ante, interviewer nee knowledge of modern, high-throughput systems ni test chestunnadu.

---

### Scenario 1: The `CompletableFuture` Performance Trap

**Interviewer:** "I have a service that uses `CompletableFuture.supplyAsync()` to make several slow, blocking network calls. I'm noticing that my application's overall performance is poor, and other unrelated async tasks are also slowing down. What is the likely cause of this problem, and what is the modern Java solution?"

**Why this question?**
This is a very practical, scenario-based question that directly tests your understanding of the `CompletableFuture` default thread pool and its limitations with blocking code.

**How to Answer:**

"This is a classic case of **common pool starvation**.

**The Diagnosis:** By default, `CompletableFuture.supplyAsync()` (without a specified executor) submits its tasks to the common `ForkJoinPool`. This is a system-wide, shared pool of platform threads with a small, fixed size (usually equal to the number of CPU cores). This pool is optimized for short, CPU-bound tasks.

When you submit a slow, blocking network call to this pool, that task occupies one of those precious platform threads for the entire duration of the I/O wait. If you submit several of these blocking tasks, you can easily use up all the threads in the common pool. At that point, the pool is "starved," and no other tasks submitted to the common pool (even from different parts of your application) can run until one of the blocking tasks completes. This creates a major, system-wide bottleneck.

**The Modern Solution:**
The modern solution, available since Java 21, is to **provide a dedicated `Executor` that uses virtual threads** for these blocking tasks.

```java
// Create an executor that creates a new virtual thread for each task.
ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

// Pass this executor to supplyAsync.
CompletableFuture.supplyAsync(() -> makeSlowNetworkCall(), virtualThreadExecutor);
```

By doing this, when the task makes the blocking network call, the JVM will unmount the virtual thread from its carrier (the OS thread), freeing up the carrier to do other work. The common `ForkJoinPool` is not affected at all. This allows us to combine the powerful, composable API of `CompletableFuture` with the massive scalability of virtual threads for I/O-bound work."

---

### Scenario 2: "Aren't They Opposites?"

**Interviewer:** "`CompletableFuture` is associated with the asynchronous, non-blocking programming style. Virtual threads are often promoted as a way to make simple, synchronous, blocking code scalable. Why would you ever use them together? Aren't they designed to solve problems for opposite programming styles?"

**Why this question?**
This is a great conceptual question to test your deeper understanding. It checks if you see these tools as competing or as complementary.

**How to Answer:**

"That's a very insightful question. It might seem like they are for opposite styles, but they are actually **highly complementary** and solve different aspects of a problem. It helps to think of them as separating the **'what'** from the **'how'**.

1.  **`CompletableFuture` (The 'What'):** It's a high-level API for **composition and orchestration**. It provides a declarative way to define *what* your asynchronous workflow is. For example, "what I want to do is: fetch a user, then fetch their orders and wishlist in parallel, then combine the results." It's about defining the dependency graph of your logic. It answers, "What is the flow of my work?"

2.  **Virtual Threads (The 'How'):** It's a low-level implementation detail for **execution**. It defines *how* a single unit of work (a task) is run. It answers, "How should this specific blocking task be executed without consuming a scarce OS thread?"

**Why Use Them Together?**
You use them together to get the best of both worlds:
*   You get the beautiful, composable, non-blocking pipeline API from `CompletableFuture` to define your complex workflow.
*   You get the massive scalability and efficiency from Virtual Threads to execute the individual (potentially blocking) steps within that workflow.

So, you are using the best tool for composition (`CompletableFuture`) combined with the best tool for scalable execution (`Virtual Threads`). They are a perfect match for building modern, high-throughput, and yet readable, server-side applications."
