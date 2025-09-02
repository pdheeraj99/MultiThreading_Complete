### Interview Questions for Module 21: Phaser

#### Core Concepts

1.  **Question:** What is the primary advantage of a `Phaser` compared to a `CyclicBarrier`?
    *   **Answer:** The primary advantage is **flexibility**. Unlike `CyclicBarrier`, where the number of parties is fixed at creation, a `Phaser` allows the number of registered parties to be changed dynamically at any time. This makes it suitable for more complex synchronization scenarios where tasks can spawn new sub-tasks that also need to participate in the barrier synchronization.

2.  **Question:** Explain the concept of a "phase" in a `Phaser`.
    *   **Answer:** A phase is a single step or round of synchronization in a `Phaser`'s lifecycle. Each phase is represented by an integer, starting from 0. When all registered parties have arrived at the barrier for the current phase, the phase number increments, and all waiting threads are released to begin work on the next phase. This allows for multi-stage coordination where threads repeatedly synchronize before moving to the next step of a larger task.

3.  **Question:** A running task needs to spawn two new helper tasks that should also participate in the next synchronization point. How would you achieve this with a `Phaser`?
    *   **Answer:** The original task would first call `phaser.bulkRegister(2)` or `phaser.register()` twice. This action increases the number of parties the phaser is waiting for. After successfully registering the new parties, the original task can then create and start the two new helper task threads. The new helper tasks will then be able to participate in the current phase (or subsequent phases) by calling one of the arrival methods.

#### API and Usage

4.  **Question:** What is the difference between `arrive()`, `arriveAndAwaitAdvance()`, and `arriveAndDeregister()`?
    *   **Answer:**
        *   `arrive()`: Signals that the calling party has completed the current phase but does **not** block. The thread continues execution immediately. This is useful for a party that wants to signal completion but doesn't need to wait for others.
        *   `arriveAndAwaitAdvance()`: This is the most common method. It signals arrival and **blocks** the calling thread until all other registered parties have also arrived. It's the primary synchronization mechanism.
        *   `arriveAndDeregister()`: Signals arrival for the current phase and simultaneously de-registers the party from the phaser. The phaser will not wait for this party in any subsequent phases. This is useful when a task has completed its work entirely.

5.  **Question:** Can a `Phaser` be used to implement the functionality of a `CountDownLatch`? How?
    *   **Answer:** Yes. You can simulate a `CountDownLatch` by creating a `Phaser` with an initial party count of 1 (`Phaser phaser = new Phaser(1);`). The main thread that would have called `latch.await()` will instead call `phaser.arriveAndAwaitAdvance()`. Other worker threads do not need to be registered initially. When a worker thread finishes its task, it first calls `phaser.register()` and then immediately `phaser.arriveAndDeregister()`. When all workers have done this, the main thread waiting in `arriveAndAwaitAdvance()` will be released. However, this is more complex than just using a `CountDownLatch`, which is the better tool for a simple one-shot gate.

6.  **Question:** What is a hierarchical phaser? What problem does it solve?
    *   **Answer:** A `Phaser` can be constructed with a `parent` phaser. This allows you to create a tree-like structure of phasers. This is an advanced feature designed for massive scalability. For example, you could have thousands of worker threads. Instead of registering all of them with a single phaser (which could become a contention point), you could group them into sub-phasers (e.g., 10 phasers with 100 threads each). Each sub-phaser would have a common parent phaser. When all parties in a sub-phaser arrive, the sub-phaser itself will act as a single party arriving at the parent phaser. This reduces contention and allows for synchronization among a very large number of parties more efficiently.

#### Scenarios

7.  **Question:** You are designing a parallel algorithm that processes data in three stages: (1) Pre-processing, (2) Main-processing, (3) Post-processing. All threads must complete stage 1 before any can move to stage 2, and so on. The number of threads is fixed. Which synchronizer would you choose: `CyclicBarrier` or `Phaser`, and why?
    *   **Answer:** In this scenario, a `CyclicBarrier` is the better choice. The number of threads (parties) is fixed, and the synchronization pattern is a simple, reusable barrier. `CyclicBarrier` has a simpler API and is perfectly suited for this exact problem. While a `Phaser` could also accomplish this, it's more complex than necessary. It's best to use the simplest tool that solves the problem effectively.
