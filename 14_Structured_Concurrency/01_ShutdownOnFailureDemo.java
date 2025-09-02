import jdk.incubator.concurrent.StructuredTaskScope;
import java.time.Duration;
import java.util.concurrent.Future;

public class Main {

    // Usecase: Fetching data from two different sources concurrently. If one source fails,
    // we want to immediately cancel the other one to save resources.
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting concurrent fetch...");
        try {
            String result = fetchUserDataAndOrder();
            System.out.println("Final Result: " + result);
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    static String fetchUserDataAndOrder() throws Exception {
        // ShutdownOnFailure ensures that if any forked task fails, all other tasks in the scope are cancelled.
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            System.out.println("Scope started. Forking tasks...");

            // Fork a task that successfully fetches user data.
            Future<String> userFuture = scope.fork(() -> {
                System.out.println("Fetching user data... (will succeed)");
                Thread.sleep(Duration.ofSeconds(1));
                return "User 'John Doe'";
            });

            // Fork a task that simulates a failure while fetching an order.
            Future<String> orderFuture = scope.fork(() -> {
                System.out.println("Fetching order data... (will fail)");
                Thread.sleep(Duration.ofMillis(500)); // This task is faster
                throw new IllegalStateException("Order service is down");
            });

            // The join() method waits until either one task fails or all tasks complete successfully.
            // In this case, it will return after orderFuture fails.
            System.out.println("Waiting for tasks to complete (or one to fail)...");
            scope.join();
            System.out.println("Join completed. Checking for failure...");

            // If a task failed, this method will propagate the exception.
            // The userFuture task will be cancelled by the scope automatically.
            scope.throwIfFailed();

            // This part of the code will not be reached because orderFuture fails.
            String user = userFuture.resultNow();
            String order = orderFuture.resultNow();

            return user + " | " + order;
        }
    }
}
