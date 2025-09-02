import jdk.incubator.concurrent.StructuredTaskScope;
import java.time.Duration;
import java.util.concurrent.Future;

public class Main {

    // Define a ScopedValue to hold the request's transaction ID.
    public static final ScopedValue<String> TRANSACTION_ID = ScopedValue.newInstance();

    // Usecase: A web server receives a request and needs to perform two concurrent operations
    // (e.g., call two different microservices). We want to propagate a unique transaction ID
    // to both operations for logging and tracing, without passing it as a method parameter.
    public static void main(String[] args) {
        // Simulate handling an incoming web request for transaction "tx-main-987"
        handleRequest("tx-main-987");
    }

    static void handleRequest(String txId) {
        // Bind the transaction ID to the scope of this request.
        ScopedValue.where(TRANSACTION_ID, txId).run(() -> {
            System.out.println("Request Handler: Started processing with " + TRANSACTION_ID.get());

            try {
                // Use Structured Concurrency to perform subtasks.
                // The ScopedValue binding is automatically inherited by the child threads forked by the scope.
                processConcurrentTasks();
            } catch (Exception e) {
                System.err.println("Request Handler: " + TRANSACTION_ID.get() + " failed: " + e.getMessage());
            }

            System.out.println("Request Handler: Finished processing for " + TRANSACTION_ID.get());
        });
    }

    static void processConcurrentTasks() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork the first subtask.
            Future<String> f1 = scope.fork(() -> {
                // This code runs in a new virtual thread, but it can still access the ScopedValue.
                System.out.println("  Subtask 1: Running with " + TRANSACTION_ID.get());
                Thread.sleep(Duration.ofSeconds(1));
                // Here you would call an external service, passing the transaction ID.
                return "Result from Subtask 1";
            });

            // Fork the second subtask.
            Future<String> f2 = scope.fork(() -> {
                System.out.println("  Subtask 2: Running with " + TRANSACTION_ID.get());
                Thread.sleep(Duration.ofMillis(500));
                // It's the same value, effortlessly passed to another concurrent execution.
                return "Result from Subtask 2";
            });

            // Wait for both to complete.
            scope.join();
            scope.throwIfFailed();

            System.out.println("  Subtasks finished. Results: " + f1.resultNow() + ", " + f2.resultNow());
        }
    }
}
