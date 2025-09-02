import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    // Usecase: A typical asynchronous workflow.
    // 1. Fetch a user's data from a remote service.
    // 2. Using that user's data, fetch their orders from another service.
    // 3. When the orders are received, print them.
    // 4. If anything goes wrong at any stage, log the error.
    // This entire chain should be non-blocking.

    public static void main(String[] args) {
        // It's crucial to use a dedicated executor for I/O-bound tasks
        // to avoid starving the default ForkJoinPool.commonPool().
        ExecutorService ioExecutor = Executors.newFixedThreadPool(10);
        String userId = "user-123";

        System.out.println("Main thread: Kicking off the async pipeline...");

        CompletableFuture<Void> pipeline = CompletableFuture
                // Stage 1: Asynchronously fetch the user's name.
                .supplyAsync(() -> fetchUserName(userId), ioExecutor)

                // Stage 2: When the user's name is available, use it to fetch their orders.
                // thenApply is used for a one-to-one transformation of the result.
                .thenApply(userName -> fetchUserOrders(userName))

                // Stage 3: When the orders are available, "accept" them (consume them).
                // thenAccept is a terminal operation in a chain that doesn't return a value.
                .thenAccept(orders -> {
                    System.out.println("Pipeline thread: Successfully received orders: " + orders);
                    System.out.println("Pipeline thread: Displaying orders to the user.");
                })

                // Stage 4: Define what happens if any of the previous stages fail.
                // exceptionally() is the asynchronous equivalent of a catch block.
                .exceptionally(error -> {
                    System.err.println("Pipeline thread: An error occurred: " + error.getMessage());
                    return null; // Must return a value of the same type (Void in this case).
                });

        System.out.println("Main thread: Pipeline has been launched. Main thread is NOT blocked and can do other work.");

        // We need to wait for the pipeline to finish for this demo, otherwise the main thread
        // might exit before the async operations complete. In a real app (e.g., a web server),
        // the server thread would just return the response immediately.
        pipeline.join(); // or use a sleep, but join is more deterministic here.
        ioExecutor.shutdown();
    }

    private static String fetchUserName(String userId) {
        System.out.println("Pipeline thread: [1] Fetching user name for id: " + userId);
        try {
            Thread.sleep(1000); // Simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Uncomment the line below to simulate a failure in this stage
        // if (true) throw new RuntimeException("User service is down!");
        return "John Doe";
    }

    private static List<String> fetchUserOrders(String userName) {
        System.out.println("Pipeline thread: [2] Fetching orders for user: " + userName);
        try {
            Thread.sleep(1500); // Simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return List.of("Order-A", "Order-B", "Order-C");
    }
}
