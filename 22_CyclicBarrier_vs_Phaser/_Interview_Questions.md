### Interview Questions for Module 22: CyclicBarrier vs. Phaser

#### Head-to-Head Comparison

1.  **Question:** What is the single most important difference between `CyclicBarrier` and `Phaser` that would determine your choice between them?
    *   **Answer:** The most important difference is that `CyclicBarrier` works with a **fixed** number of parties set at creation, while `Phaser` works with a **dynamic** number of parties that can change at any time. Your choice should primarily be based on whether the number of threads you need to synchronize is constant or variable.

2.  **Question:** You need to execute a specific `Runnable` task every time a group of threads reaches a synchronization point. How would you implement this "barrier action" using a `CyclicBarrier` versus a `Phaser`? Which is simpler?
    *   **Answer:**
        *   With `CyclicBarrier`, it's very simple. You pass the `Runnable` action directly into the constructor: `new CyclicBarrier(parties, barrierAction)`.
        *   With `Phaser`, there is no direct constructor for a barrier action. You must create a subclass of `Phaser` and override the `onAdvance(int phase, int registeredParties)` method. The logic of your action goes inside this method.
        *   For this specific requirement, `CyclicBarrier`'s approach is simpler and more direct.

3.  **Question:** Can a `Phaser` do everything a `CyclicBarrier` can do? Can a `CyclicBarrier` do everything a `Phaser` can do?
    *   **Answer:**
        *   Yes, a `Phaser` can do everything a `CyclicBarrier` can. It can handle a fixed number of parties and can simulate a barrier action by overriding `onAdvance()`.
        *   No, a `CyclicBarrier` cannot do everything a `Phaser` can. It cannot handle a dynamic number of parties, which is the key feature of a `Phaser`. `Phaser` is a more general and powerful tool.

#### Scenario-Based Questions

4.  **Question:** **Scenario 1:** You are building a parallel testing framework where 50 threads each run a test case. After all 50 tests are complete, a separate "aggregator" thread must be triggered to collect and summarize the results. The set of 50 threads is the same for every run. Which synchronizer do you choose and why?
    *   **Answer:** `CyclicBarrier` is the ideal choice here.
        *   **Why:** The number of parties (50 test threads) is fixed. The requirement to trigger a specific action (the aggregator) after all threads arrive is a perfect match for `CyclicBarrier`'s barrier action feature. The API is simpler and directly fits the problem.

5.  **Question:** **Scenario 2:** You are designing a parallel web crawler. You start with 5 crawler threads. As these crawlers find pages with more links, they spawn new crawler threads to help with the work. You need a way to ensure that all active crawlers (both original and newly spawned) finish crawling all pages at "depth 1" before any of them move on to "depth 2". Which synchronizer do you choose and why?
    *   **Answer:** `Phaser` is the only choice that works well here.
        *   **Why:** The number of parties (crawler threads) is dynamic. `CyclicBarrier` cannot handle this. `Phaser` is designed for this exact use case, allowing new crawler tasks to `register()` with the phaser as they are created and for all active parties to synchronize at the end of each phase (depth level) using `arriveAndAwaitAdvance()`.

6.  **Question:** You chose a `CyclicBarrier` for a project, but now there's a new requirement: if any of the worker threads fails with an exception, all other threads waiting at the barrier should be released immediately and stop working. Does `CyclicBarrier` support this?
    *   **Answer:** Yes, `CyclicBarrier` supports this automatically. If a thread waiting at the barrier is interrupted or times out, the barrier is considered "broken." All other threads waiting at the barrier will be released immediately with a `BrokenBarrierException`. Your code in the `catch` block for this exception should then handle the cleanup and termination of the task.

7.  **Question:** If `Phaser` is more powerful and flexible, why would anyone still use `CyclicBarrier`?
    *   **Answer:** Simplicity and intent. For a large number of common, fixed-party synchronization problems, `CyclicBarrier` is sufficient, and its API is much simpler and easier to understand and use correctly. Using a `Phaser` for a simple fixed-party problem adds unnecessary complexity (e.g., overriding `onAdvance` vs. just passing a `Runnable`). It's a good design principle to use the simplest tool that correctly solves the problem. `CyclicBarrier` more clearly communicates the *intent* that the number of parties is expected to be constant.
