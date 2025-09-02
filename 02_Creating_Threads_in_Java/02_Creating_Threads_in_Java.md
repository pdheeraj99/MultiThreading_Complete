# 2. Creating Threads in Java - మన "Worker" ని Code లోకి ఎలా తీసుకురావాలి?

Mawa, welcome back! Last chapter lo manam oka pedda problem (unresponsive UI) ni chusam and daani solution concurrency ani telusukunnam. Ippudu asalu question: How do we actually create these "workers" (threads) in Java?

## The "What they thought" story: The `Thread` Class
Java 1.0 lo, creators chala simple ga alochincharu: "If you want a thread, you should create a `Thread`." They created the `java.lang.Thread` class. If you wanted to create a task that runs on a new thread, the most direct way was to **extend** the `Thread` class and put your logic in the `run()` method. Your object *was* a thread.

## The "What happened next" story: The `Runnable` Interface
Developers quickly ran into a major problem. Java only allows a class to extend **one** other class. What if you wanted to create a UI component, like a `JPanel`, that also needed to run some code in the background? It already `extends JPanel`, so it **cannot** also `extends Thread`.

To solve this, the concept of **`Runnable`** was promoted as a best practice. This separated the **task** (the `Runnable`) from the **worker** (the `Thread`). This is a much better design.

---

## The Two Basic Ways to Create a "Worker" in Java

So, today we have two primary ways.

1.  **`implements Runnable`**: The "Composition" approach. (Highly Recommended ⭐)
2.  **`extends Thread`**: The "Inheritance" approach. (Generally Avoided)

Let's explore why one is so much better than the other.

---

### Strategy 1: Implementing `Runnable` (The Preferred Way ⭐)

This is the best and most widely-used approach. It follows a core software design principle: **Composition over Inheritance**.

**The Idea:** "Pani" ni "Pani chese vaadini" separate cheyadam.
*   **Your class HAS-A task.** You create a class that `implements Runnable`. This means your class *has* a task that can be run. The task logic goes into the `run()` method.
*   **The `Thread` is the worker.** You then create a separate `Thread` object and give it your `Runnable` task. The `Thread` is the worker that executes the task.
*   **Analogy**: Nuvvu oka to-do list (`Runnable`) create chestav. Tarvata, oka worker (`Thread`) ni pilichi, ee to-do list vaadi chetiki isthav.
*   **Advantage**: Super flexible! Nee class inko class ni extend cheskovachu, problem ledu. Pani veru, pani chesevadu veru. This is clean design.

```java
class MyTask implements Runnable {
    public void run() { /* Your task logic */ }
}

// In your main code:
MyTask task = new MyTask();
Thread worker = new Thread(task);
worker.start();
```

### Strategy 2: Extending `Thread` (The Old Way)

This approach is simpler for trivial examples but is considered bad practice for real applications.

**The Idea:** Your class **is a** thread.
*   **Analogy**: Nuvve worker vi. Nuvve pani cheyali.
*   **How**: You `extend Thread` and put your task logic inside the `run()` method.
*   **BIG Disadvantage**: As we discussed, this uses up your one and only "extends" slot. It tightly couples your logic to the `Thread` class, making it inflexible and harder to test.

```java
class MyThread extends Thread {
    public void run() { /* Your task logic */ }
}

// In your main code:
MyThread worker = new MyThread();
worker.start();
```

---
## The "What about `Callable`?" Teaser

You might have heard of a third way, `Callable`, for tasks that return a result.

**The "What they thought" story:** `Runnable`'s `run()` method is `void`. What if a background task needs to return a value, like the result of a database query?

**The "What happened next" story:** The `Callable` interface was created. Its `call()` method returns a value and can throw checked exceptions.

> **🚨 Advanced Preview Note:** `Callable` ni run cheyadaniki and daani result ni theeskovadaniki, manaki `Future` and `ExecutorService` lanti advanced tools kavali. We will introduce `Callable` properly in **Chapter 8**, where we learn about the `ExecutorService`. For now, just know that it exists for tasks that need to return a result.

## What's Next? (తదుపరి ఏమిటి?)

Super, mawa! Ippudu manaki a worker thread ni ela create cheyalo telisindi, and `Runnable` enduku better approach o kuda ardhamaindi.

But what happens to a worker after we hire them? Do they start working immediately? Can they take a break? What happens when their work is done?

This is the **Thread Lifecycle**. A thread goes through several states in its life. Understanding these states is crucial for debugging and managing complex concurrent applications. And that's our next chapter: **`03_Thread_Lifecycle`**. Let's go and find out! 👋
