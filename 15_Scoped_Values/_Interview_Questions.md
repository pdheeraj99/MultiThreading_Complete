### Interview Questions for Module 15: Scoped Values

#### Core Concepts

1.  **Question:** What is the fundamental problem with `ThreadLocal` that Scoped Values aim to solve?
    *   **Answer:** The fundamental problem is that `ThreadLocal`'s lifetime is tied to the thread, not to a specific operation. This leads to several critical issues:
        *   **Data Leaks/Corruption:** In thread pools, if `.remove()` isn't called in a `finally` block, data from one request can leak into another request that reuses the same thread.
        *   **Memory Leaks:** With millions of virtual threads, `ThreadLocal` can cause huge memory consumption because the data persists for the thread's entire life.
        *   **Mutability:** `ThreadLocal` variables are mutable, which can lead to unpredictable behavior and hard-to-debug issues.

2.  **Question:** How do Scoped Values solve these problems? Explain their core mechanism.
    *   **Answer:** Scoped Values tie the lifetime of a value to a lexical scope of code execution, not to a thread.
        *   **No Leaks:** The value is only bound for the duration of a `run()` or `call()` method. It is automatically "removed" when the code block exits, making leaks impossible.
        *   **Efficiency:** They are designed to be efficiently inherited by child threads, especially virtual threads, avoiding the memory-per-thread problem.
        *   **Immutability:** Once a Scoped Value is bound, it cannot be changed within that scope, ensuring predictable data flow. To change a value, one must create a new, nested scope.

3.  **Question:** What does it mean for a Scoped Value to be "dynamically scoped"?
    *   **Answer:** "Dynamically scoped" means the value that a piece of code reads depends on the most recent binding established in its dynamic call stack, not on where the code is lexically written. When `myScopedValue.get()` is called, the JVM walks up the call stack to find the innermost `ScopedValue.where(...)` binding for that specific value. This allows a deeply nested method to access a value set by a top-level caller without the value being passed through every intermediate method's parameters.

#### Scenarios and Usage

4.  **Question:** Show how you would define and use a Scoped Value to pass a `TransactionID` to a service layer method.
    *   **Answer:**
        ```java
        // 1. Define the Scoped Value
        public static final ScopedValue<String> TRANSACTION_ID = ScopedValue.newInstance();

        // 2. In the top-level method (e.g., a controller)
        public void handleRequest(String txId) {
            // 3. Bind the value and run the code
            ScopedValue.where(TRANSACTION_ID, txId)
                       .run(() -> myService.process());
        }

        // 4. In the service layer (or any nested call)
        public class MyService {
            public void process() {
                // 5. Get the value
                String currentTxId = TRANSACTION_ID.get();
                System.out.println("Processing with TID: " + currentTxId);
            }
        }
        ```

5.  **Question:** How do Scoped Values interact with Structured Concurrency? If I fork multiple tasks inside a `StructuredTaskScope`, do they get access to the Scoped Value?
    *   **Answer:** Yes, they interact seamlessly, and this is one of their primary use cases. If you bind a Scoped Value and then create a `StructuredTaskScope` within that scope, any tasks forked by the scope will automatically and efficiently inherit the binding. This is the modern, preferred way to provide contextual data to concurrent subtasks.

    ```java
    ScopedValue.where(USER, "admin").run(() -> {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                // This child thread can access USER.get() and it will be "admin"
                return database.query();
            });
            // ...
        }
    });
    ```

6.  **Question:** Can you rebind a Scoped Value to a new value within an existing scope? What happens?
    *   **Answer:** You cannot change the value of an existing binding. Scoped Values are immutable within their scope. However, you can create a *new, nested scope* that temporarily "shadows" the outer value with a new one.

    ```java
    ScopedValue.where(USER, "user-A").run(() -> {
        System.out.println(USER.get()); // Prints "user-A"

        // Create a new, nested binding
        ScopedValue.where(USER, "user-B-temp").run(() -> {
            System.out.println(USER.get()); // Prints "user-B-temp"
        });

        System.out.println(USER.get()); // Prints "user-A" again. The outer binding was unaffected.
    });
    ```

#### Comparison

7.  **Question:** Why not just use method parameters to pass data? When is a Scoped Value a better choice?
    *   **Answer:** Using method parameters is the most explicit and often the best approach. However, it becomes cumbersome for "cross-cutting concerns" or ambient context that is needed by many different layers of an application (e.g., transaction ID, user credentials, security context, locale). Adding this context to every single method signature clutters the API and tightly couples distant components. Scoped Values are a better choice when you need to provide this kind of ambient, request-specific context to a deep and wide call tree without polluting method signatures. They decouple the top-level provider of the context from the deep-level consumer.
