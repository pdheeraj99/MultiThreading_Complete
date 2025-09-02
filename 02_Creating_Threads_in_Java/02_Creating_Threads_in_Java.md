# 2. Creating Threads in Java - మన "Worker" ని Code లోకి ఎలా తీసుకురావాలి?

Mawa, welcome back! Last chapter lo manam oka pedda problem ni identify chesam: The Unresponsive UI 🥶. Manam solution kuda kanukkunnam: oka separate "worker thread" ni create chesi, time-consuming pani antha daaniki అప్పగించాలి (delegate cheyali).

So, the big question now is...

## The Problem: How Do We Actually Create a Thread? 🤔

Concept bagundi, kani daanini code lo ki ela translate cheyali? How do we tell Java, "Hey, naku oka kotha worker kavali!"? Mana photo editor example lo, "Apply Filter" pani cheyadaniki aa kotha worker ni ela create cheyali?

Java lo threads create cheyadaniki manaki konni options unnayi. వాటిని మూడు simple scenarios laaga chuddam.

## The Solutions: The Three Ways to Create a "Worker" in Java

1.  **Nuvve Worker la Maaradam (Extending `Thread` class)**
2.  **Worker ki To-Do List Ivvadam (Implementing `Runnable` interface)** (Most common and recommended! ⭐)
3.  **Worker ni Report Adagadam (Implementing `Callable` interface)**

Let's explore each of these strategies. Prathi approach ki code example manam separate files (`1_ExtendingThread.java`, etc.) lo chuddam.

---

### Strategy 1: Nuvve Worker la Maaradam (Extending `Thread`)

Ee approach lo, nee class ye oka worker (thread) laaga maaripothundi.

**The Idea:**
Nuvvu `java.lang.Thread` class ni direct ga extend chesi, "Nene oka Thread ni!" ani cheptunnav.
*   **Analogy**: Simple ga, nuvve worker vi. Nuvve pani cheyali.
*   **How**: You `extend Thread` and put your task logic inside the `run()` method.
*   **Advantage**: Chala simple ga anipistundi for basic examples.
*   **BIG Disadvantage**: Java lo manam okate class ni extend cheyagalam. So, nee class ki verey class (e.g., `JFrame`) ni extend cheyalsi vasthe, ee approach paniki raadu. Nuvvu okesari rendu panulu cheyaleni worker vi aipothav! Anduke idi recommend cheyaru.

```mermaid
graph LR
    subgraph "Strategy 1: Extending Thread"
        A(YourClass) -- is a --> B(Thread);
        A -- must do the work in --> C(run() method);
    end
```

---

### Strategy 2: Worker ki To-Do List Ivvadam (Implementing `Runnable`) ⭐

This is the best and most widely-used approach. Ikkada nuvvu worker vi kaadu, nuvvu manager vi. Nuvvu "pani ento" define chesi, adi cheyadaniki oka worker ni theeskuntav.

**The Idea:**
"Pani" ni "Pani chese vaadini" separate cheyadam. This is a core software design principle.
*   **Analogy**: Nuvvu oka to-do list (`Runnable`) create chestav. Andulo em cheyalo `run()` method lo raasthav. Tarvata, oka worker (`Thread`) ni pilichi, ee to-do list vaadi chetiki isthav. Aa worker pani antha aa list prakarame chestadu.
*   **How**: Your class `implements Runnable`. Then you create a `new Thread(yourRunnableObject)` and start it.
*   **Advantage**: Super flexible! Nee class inko class ni extend cheskovachu, problem ledu. Pani veru, pani chesevadu veru. This is clean and beautiful design. Idi "separation of concerns" ane pedda design principle ni follow avtundi.

```mermaid
graph LR
    subgraph "Strategy 2: Implementing Runnable"
        A(YourClass) -- has a --> B(Runnable Task);
        B -- defines work in --> C(run() method);
        D(Thread) -- is given --> B;
    end
```

---

### Strategy 3: Worker ni Report Adagadam (Implementing `Callable`)

Pani cheyadam matrame kaadu, aa pani ayyaka result kuda kavali anukunappudu idi vaadali.

> **🚨 Advanced Preview Note:** `Callable` ni run cheyadaniki standard and best way `ExecutorService` ni vaadadam. Ee `ExecutorService` gurinchi manam **Chapter 8** lo detail ga nerchukuntam. For now, just remember that this option exists for tasks that need to return a value. The code example will show a preview of this.

**The Idea:**
Use this when your background task needs to return a value.
*   **Analogy**: Nuvvu oka worker ki pani cheppi, "Pani ayyaka, emaindo naku cheppu" ani adagadam lanti di. Aa worker (`Callable`) pani chesi, neeku oka result (`return value`) isthadu. Aa result eppudu vastundo cheppadaniki, neeku mundugaane oka promise slip (`Future`) istharu.
*   **How**: Your class `implements Callable<V>`, where `V` is the type of the result (e.g., `String`, `Integer`). The task logic goes into the `call()` method, which returns a value.
*   **Key Difference**: `Runnable`'s `run()` method emi return cheyadu (`void`). `Callable`'s `call()` method oka value ni return chestundi. Also, `call()` can throw exceptions, which is great for error handling.

## So, How Does This Solve Our Problem?

Let's go back to our original problem: the frozen photo editor.

1.  **The "Apply Filter" logic**: Ee logic ni manam oka `Runnable` object lo pedatham.
    ```java
    // Idi mana To-Do list
    class ApplyFilterTask implements Runnable {
        public void run() {
            // Time theeskune filter logic ikkada...
            System.out.println("Filter apply cheyadam aipoindi!");
        }
    }
    ```
2.  **Hiring the Worker**: User button click chesinappudu, UI thread (manager) ee pani chestundi:
    ```java
    // User clicks button...
    Runnable task = new ApplyFilterTask(); // 1. To-do list theesko
    Thread worker = new Thread(task);      // 2. Kotha worker ni a list tho hire chey
    worker.start();                        // 3. Pani start cheyamani cheppu!
    ```

Anthe! The UI thread is now free. The `worker` thread will handle the `ApplyFilterTask` in the background. Problem solved! ✅

## What's Next? (తదుపరి ఏమిటి?)

Super, mawa! Ippudu manaki a frozen UI problem ni solve cheyadaniki kaavalsina theoretical knowledge vachesindi. We know *how* to create our workers.

But what happens to a worker after we hire them? Do they start working immediately? Can they take a break? What happens when their work is done?

This is the **Thread Lifecycle**. A thread goes through several states in its life. Understanding these states is crucial for debugging and managing complex concurrent applications. And that's our next chapter: **`03_Thread_Lifecycle`**. Let's go and find out! 👋
