# 💬 Interview Questions & Answers - Topic 11: CompletableFuture

Mawa, `CompletableFuture` anedi modern Java interviews lo oka hot topic. Idi vachante, nuvvu just multithreading ye kaadu, asynchronous programming lo kuda strong ani ardam.

---

### Scenario 1: The Blocking `Future`

**Interviewer:** "What is the main limitation of a traditional `Future` that you get from an `ExecutorService`, and how does `CompletableFuture` solve this?"

**Why this question?**
This question tests if you understand the core motivation for `CompletableFuture`'s existence. It's about the "why," not just the "what."

**How to Answer:**

"The main limitation of a traditional `Future` is that its `get()` method is **blocking**. If you need the result of the future to perform another task, you are forced to call `future.get()` and block your current thread until the result is available. This completely defeats the purpose of running the task asynchronously, as it turns an asynchronous execution into a synchronous workflow. It makes composing multiple dependent asynchronous steps very difficult and inefficient.

`CompletableFuture` solves this problem by providing a **non-blocking, declarative, and composable API**.

Instead of blocking and waiting for a result, you can attach a **callback** or a **stage** to the `CompletableFuture` that will be automatically executed when the result is ready. This is done through a rich set of methods like:
*   **`thenApply(function)`:** To transform the result.
*   **`thenAccept(consumer)`:** To do something with the result.
*   **`thenCompose(function)`:** To chain another asynchronous operation.
*   **`thenCombine(otherFuture, ...)`:** To combine the result of two independent futures.
*   **`exceptionally(function)`:** To handle any errors that occur in the pipeline.

This allows you to build complex, multi-step asynchronous workflows that are non-blocking from end to end, leading to much more efficient and responsive applications."

---

### Scenario 2: `thenApply` vs. `thenCompose`

**Interviewer:** "That's a great overview. Can you explain the difference between `thenApply` and `thenCompose`? When would you use one over the other?"

**Why this question?**
This is the most common technical question about `CompletableFuture`. It's a classic "gotcha" that quickly separates candidates who have actually used the API from those who have only read about it.

**How to Answer:**

"Yes, this is a crucial difference to understand for building clean pipelines. Both are used for chaining dependent operations, but they are used in different scenarios based on the **return type** of the function you are applying.

**1. `thenApply(Function<T, U> fn)`:**
   - Use `thenApply` when your chaining function takes a value of type `T` and returns a **plain value** of type `U`. It's for simple, synchronous transformations.
   - **Example:** You have a `CompletableFuture<User>`, and you want to get the user's email address (a `String`). Your function is `user -> user.getEmail()`, which is a `Function<User, String>`.
   ```java
   CompletableFuture<User> userFuture = getUserById(123);
   CompletableFuture<String> emailFuture = userFuture.thenApply(user -> user.getEmail());
   ```

**2. `thenCompose(Function<T, CompletableFuture<U>> fn)`:**
   - Use `thenCompose` when your chaining function takes a value of type `T` but returns **another `CompletableFuture`** of type `U`. This is used when the next step is *also* an asynchronous operation.
   - `thenCompose` "flattens" the result. If you used `thenApply` in this case, you would get a messy `CompletableFuture<CompletableFuture<Orders>>`. `thenCompose` intelligently unwraps it for you, giving you a clean `CompletableFuture<Orders>`.
   - **Example:** You have a `CompletableFuture<User>`, and your next step is to call another async method, `getOrdersForUser(user)`, which itself returns a `CompletableFuture<List<Order>>`.
   ```java
   CompletableFuture<User> userFuture = getUserById(123);
   // The function here returns a CompletableFuture, so we use thenCompose.
   CompletableFuture<List<Order>> ordersFuture = userFuture.thenCompose(user -> getOrdersForUser(user));
   ```

**In short:**
*   `thenApply` = `map` (for synchronous transformations).
*   `thenCompose` = `flatMap` (for asynchronous transformations, to avoid nested futures)."

**Pro Tip 💡:**
Using the `map` vs. `flatMap` analogy from Java Streams is a very powerful way to explain the difference. It shows you can connect concepts from different parts of the Java ecosystem and have a deep understanding of functional composition.
