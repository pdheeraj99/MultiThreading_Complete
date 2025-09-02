### Interview Questions for Module 23: Exchanger

#### Core Concepts

1.  **Question:** What is the primary purpose of the `java.util.concurrent.Exchanger` class?
    *   **Answer:** The primary purpose of an `Exchanger` is to provide a synchronization point, or a "rendezvous," for **exactly two threads** to meet and **swap objects**. It's a specialized tool for two-party data exchange.

2.  **Question:** What happens when a thread calls the `exchange(V x)` method on an `Exchanger`?
    *   **Answer:** The thread blocks and waits until a second thread calls the `exchange()` method on the same `Exchanger` instance. Once the second thread arrives, the `Exchanger` performs the swap: the object provided by the first thread is given to the second thread (as its return value), and the object from the second thread is given to the first. Both threads are then unblocked.

3.  **Question:** What happens if a thread calls `exchange()` but a partner thread never arrives?
    *   **Answer:** The first thread will block indefinitely (or until it is interrupted). To prevent this, you can use the overloaded version of the `exchange(V x, long timeout, TimeUnit unit)` method, which will throw a `TimeoutException` if a partner thread does not arrive within the specified timeout period.

4.  **Question:** How many threads can participate in a single exchange? What would happen if a third thread tried to join?
    *   **Answer:** An `Exchanger` is strictly for **two** threads. If a third thread calls `exchange()`, it will be treated as the first thread of a *new* exchange pair and will block, waiting for a fourth thread that will likely never arrive. The `Exchanger` does not create three-way exchanges; it only handles pairs.

#### Scenarios and Use Cases

5.  **Question:** Describe a classic use case for an `Exchanger`.
    *   **Answer:** The classic use case is a **producer-consumer scenario using double buffering**. A producer thread fills a buffer with data. A consumer thread processes a buffer of data. When the producer's buffer is full and the consumer's buffer is empty, they meet at the `Exchanger`. The producer trades its full buffer for the consumer's empty one. This allows both threads to continue working in parallel with minimal delay—the producer can immediately start filling the new empty buffer while the consumer starts processing the new full one.

6.  **Question:** You need to implement the buffer-swapping pattern described above. Why might an `Exchanger` be a better choice than using two `BlockingQueue`s (one for full buffers, one for empty buffers)?
    *   **Answer:** While two `BlockingQueue`s would work, an `Exchanger` is often a better choice for this specific pattern because:
        *   **Simplicity and Intent:** `Exchanger` is a simpler, more direct tool that perfectly expresses the *intent* of a two-party swap. The code is often cleaner and easier to understand.
        *   **Reduced Overhead:** An `Exchanger` is a lighter-weight synchronization mechanism compared to two full `BlockingQueue`s, which each have their own internal locks, condition queues, and node management. For a simple swap, the `Exchanger` can be more performant.
        *   **Synchronization Guarantee:** The `Exchanger` guarantees a direct rendezvous. The producer knows it is handing its buffer directly to an active consumer. With queues, the producer just drops the buffer off and doesn't know when, or if, the consumer will pick it up.

7.  **Question:** Is `Exchanger` a good tool for one-way, asynchronous communication from a producer to a consumer? Why or why not?
    *   **Answer:** No, it is not a good tool for that. `Exchanger` is fundamentally for a **two-way swap**. Both threads must provide an object to the `exchange()` method. If you only need to send data one way, a `BlockingQueue` (like `ArrayBlockingQueue` or `LinkedBlockingQueue`) or a `SynchronousQueue` is a much more appropriate and efficient tool for the job. Using an `Exchanger` would require the consumer to create and pass a dummy object just to complete the exchange, which is inefficient and confusing.
