import jdk.incubator.concurrent.StructuredTaskScope;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Main {

    // Usecase: We need a piece of data that can be fetched from multiple redundant/backup
    // sources. We want the result from the FIRST source that responds successfully and
    // want to cancel all other requests immediately to save resources.
    public static void main(String[] args) {
        System.out.println("Querying multiple services for the fastest response...");
        try {
            String result = findFirstSuccessfulResponse();
            System.out.println("Success! Fastest service responded with: " + result);
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    static String findFirstSuccessfulResponse() throws Exception {
        // ShutdownOnSuccess waits for the first successful task and then cancels all others.
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            System.out.println("Scope started. Forking tasks to redundant services...");

            // Fork a task to a slow but reliable service.
            scope.fork(fetchFrom("Service A (slow)", Duration.ofSeconds(2), "Response from A", true));

            // Fork a task to a fast but sometimes unavailable service.
            scope.fork(fetchFrom("Service B (fast)", Duration.ofMillis(500), "Response from B", true));

            // Fork a task to a service that is currently down.
            scope.fork(fetchFrom("Service C (failed)", Duration.ofMillis(200), "Response from C", false));

            // The join() method waits until the first task completes successfully.
            // In this case, it will be Service B.
            System.out.println("Waiting for the first successful response...");
            scope.join();

            // The result() method returns the result of the first successfully completed task.
            // It will throw an exception if all tasks failed.
            // Service A and C will be automatically cancelled by the scope.
            return scope.result();
        }
    }

    static Callable<String> fetchFrom(String serviceName, Duration delay, String response, boolean shouldSucceed) {
        return () -> {
            System.out.println("--> " + serviceName + ": Starting fetch...");
            Thread.sleep(delay);
            if (shouldSucceed) {
                System.out.println("<-- " + serviceName + ": Responding successfully.");
                return response;
            } else {
                System.out.println("<-- " + serviceName + ": Failing.");
                throw new IllegalStateException(serviceName + " is down");
            }
        };
    }
}
