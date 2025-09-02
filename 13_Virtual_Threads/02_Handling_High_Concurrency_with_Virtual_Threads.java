import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    private static final int NUM_TASKS = 100_000;

    // Usecase: Demonstrates how virtual threads can handle a massive number of concurrent I/O-bound tasks
    // more efficiently than platform threads.
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting platform thread test...");
        Instant startPlatform = Instant.now();

        // Using a traditional fixed thread pool which is limited by the number of platform threads.
        try (var executor = Executors.newFixedThreadPool(200)) {
            IntStream.range(0, NUM_TASKS).forEach(i -> {
                executor.submit(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(1)); // Simulate I/O-bound work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } // executor.close() is called automatically, which waits for tasks to complete

        Instant endPlatform = Instant.now();
        System.out.println("Platform threads test took: " + Duration.between(startPlatform, endPlatform).toSeconds() + " seconds");

        System.out.println("\nStarting virtual thread test...");
        Instant startVirtual = Instant.now();

        // Using a virtual thread per task executor. The JVM handles the mapping of millions of virtual threads
        // to a small number of platform threads.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, NUM_TASKS).forEach(i -> {
                executor.submit(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(1)); // Simulate I/O-bound work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } // executor.close() is called automatically, which waits for tasks to complete

        Instant endVirtual = Instant.now();
        System.out.println("Virtual threads test took: " + Duration.between(startVirtual, endVirtual).toSeconds() + " seconds");
    }
}
