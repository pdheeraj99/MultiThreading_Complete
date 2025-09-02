# 💬 Interview Questions & Answers - Topic 2: Creating Threads

Mawa, ee topic nunchi the most common and fundamental interview question okati undi. Adi clear ga answer cheste, interviewer ki nee basics chala strong ani ardham aipotundi.

---

### The Ultimate Question: `implements Runnable` vs. `extends Thread`

**Interviewer:** "Java lo threads create cheyadaniki two basic ways unnayi: `extends Thread` and `implements Runnable`. Which one is better, and why? Can you give me a scenario where one is impossible to use?"

**Why this question?**
This is not just a theory question. It tests your understanding of core Java design principles (Composition over Inheritance) and language limitations (Single Inheritance). Your answer reveals your depth as a Java developer.

**How to Answer:**

"That's a great question. While both approaches work, **implementing `Runnable` is almost always the better and preferred approach.** The reason comes down to good software design and a key limitation in Java.

Here's the breakdown:

**1. The Design Reason: Composition over Inheritance**
   - When you `implements Runnable`, you are separating the **task** (what needs to be done, the code in the `run()` method) from the **worker** (the `Thread` object that executes it). Your class is a task that a thread can run. This is flexible and follows the principle of "Composition over Inheritance." It's good design.
   - When you `extends Thread`, you are mixing the task and the worker into one. Your class **is a** thread. This tightly couples your task to the `Thread` class, which is less flexible.

**2. The Technical Reason: Single Inheritance**
   - This is the knockout punch. Java classes can only **extend one parent class**.
   - If your class already extends another class (for example, `class MyPanel extends JPanel`), you **cannot** also `extend Thread`. Your only option is to `implements Runnable`.
   - This is a very common scenario in GUI programming, frameworks, and many other real-world situations.

**The Scenario Where `extends Thread` is Impossible:**
The scenario you asked for is exactly this: If I have a class that must inherit from another class, say `BaseComponent`, I cannot use the `extends Thread` approach.

```java
// This is NOT possible in Java, it won't compile.
class MyComponent extends BaseComponent, Thread { }

// The correct way is to implement Runnable.
class MyComponent extends BaseComponent implements Runnable {
    public void run() {
        // ... my task logic
    }
}
```

**Conclusion:**
So, because `implements Runnable` results in a cleaner design and doesn't consume our single 'extends' slot, it is the superior and more flexible approach for creating threads in almost all cases."

**Pro Tip 💡:**
Using the phrase "Composition over Inheritance" and explaining the "IS-A" vs. "HAS-A" relationship (Your class IS-A Thread vs. Your class HAS-A Runnable task) will make your answer stand out and show a deep understanding of object-oriented design.

---
Ee okka question ki intha depth tho answer isthe, interviewer flat aipothadu mawa! Keep it up! 🔥
