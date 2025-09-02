import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {

    // Usecase: A shared counter that multiple threads need to increment.
    // A simple 'volatile int' is NOT enough here, because 'count++' is not an atomic operation.
    // 'count++' is actually three separate operations:
    // 1. Read the current value of count.
    // 2. Add one to the value.
    // 3. Write the new value back.
    // Two threads could read the same value at the same time, both increment it, and one write would be lost.

    // The modern, safe, and performant solution is to use an AtomicInteger.
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 10;
        int incrementsPerThread = 1000;

        System.out.println("Incrementing an AtomicInteger " + (numberOfThreads * incrementsPerThread) + " times using " + numberOfThreads + " threads.");

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Each of the 10 threads will try to increment the counter 1000 times.
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    // incrementAndGet() is an atomic operation. It guarantees that the read,
                    // modify, and write sequence happens as a single, indivisible unit.
                    // No other thread can interfere in the middle of it.
                    atomicCounter.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        int expectedCount = numberOfThreads * incrementsPerThread;
        int actualCount = atomicCounter.get();

        System.out.println("Expected final count: " + expectedCount);
        System.out.println("Actual final count:   " + actualCount);

        if (expectedCount == actualCount) {
            System.out.println("Success! The count is correct. AtomicInteger prevented lost updates.");
        } else {
            System.out.println("Failure! The count is incorrect. This demonstrates a race condition that shouldn't happen with AtomicInteger.");
        }
    }
}
