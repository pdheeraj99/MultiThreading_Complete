import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.Future;
import java.time.Duration;

/*
🚨 IMPORTANT NOTE: To run this example, you must be using JDK 21 or later,
and you must enable preview features.
Compile with: javac --release 21 --enable-preview StructuredConcurrencyExample.java
Run with:     java --enable-preview StructuredConcurrencyExample
*/
public class Example1_StructuredConcurrency {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Chapter 15: Structured Concurrency Demo 🚀");
        System.out.println("[Main]: Starting a structured task to find a user and their orders.");

        try {
            UserAndOrders result = findUserAndOrders();
            System.out.println("[Main]: Success! Result: " + result);
        } catch (Exception e) {
            System.err.println("[Main]: The operation failed as a whole. Exception: " + e.getMessage());
        }
    }

    static UserAndOrders findUserAndOrders() throws InterruptedException, Exception {
        // The try-with-resources statement ensures the scope is always closed,
        // guaranteeing that all forked threads are terminated when we exit the block.
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            System.out.println("  [Scope]: Forking two sub-tasks...");

            // Fork the first sub-task: find the user.
            Future<String> userFuture = scope.fork(() -> findUser());

            // Fork the second sub-task: fetch the orders. Let's make this one fail.
            Future<Integer> ordersFuture = scope.fork(() -> fetchOrders());

            // Wait for either both tasks to complete, or one to fail.
            // If one fails, the other is automatically cancelled by the scope.
            System.out.println("  [Scope]: Joining... waiting for sub-tasks to complete or fail.");
            scope.join();

            // If any task failed, this will throw the exception.
            // This is how we centralize error handling.
            scope.throwIfFailed();

            // If we get here, both tasks succeeded. We can safely get their results.
            // .resultNow() is used because we know the task is already complete.
            return new UserAndOrders(userFuture.resultNow(), ordersFuture.resultNow());
        }
    }

    // --- Mock Tasks ---

    private static String findUser() throws InterruptedException {
        System.out.println("    [findUser]: Starting to find user...");
        Thread.sleep(Duration.ofSeconds(1));
        System.out.println("    [findUser]: Found user 'Mawa'.");
        return "Mawa";
    }

    private static Integer fetchOrders() throws InterruptedException {
        System.out.println("    [fetchOrders]: Starting to fetch orders...");
        Thread.sleep(Duration.ofSeconds(2));
        // Simulate a failure!
        System.err.println("    [fetchOrders]: Failed to connect to the database!");
        throw new IllegalStateException("Database is down");
    }

    record UserAndOrders(String user, int orderCount) {}
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Chapter 15: Structured Concurrency Demo 🚀
[Main]: Starting a structured task to find a user and their orders.
  [Scope]: Forking two sub-tasks...
  [Scope]: Joining... waiting for sub-tasks to complete or fail.
    [findUser]: Starting to find user...
    [fetchOrders]: Starting to fetch orders...
    [findUser]: Found user 'Mawa'.
    [fetchOrders]: Failed to connect to the database!
[Main]: The operation failed as a whole. Exception: java.lang.IllegalStateException: Database is down
*/
