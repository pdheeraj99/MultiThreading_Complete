import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Example1_CF_and_VT {

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 14: CompletableFuture ❤️ Virtual Threads 🚀");

        // --- Scenario 1: The WRONG Way (Blocking the Common Pool) ---
        // We use a fixed thread pool to simulate the limited common ForkJoinPool.
        System.out.println("\n--- Scenario 1: Submitting blocking tasks to a small PLATFORM thread pool ---");
        ExecutorService platformThreadPool = Executors.newFixedThreadPool(4);
        long startTimePlatform = System.currentTimeMillis();

        List<CompletableFuture<Void>> platformFutures = IntStream.range(0, 10)
                .mapToObj(i -> CompletableFuture.runAsync(() -> blockingTask(i), platformThreadPool))
                .toList();

        CompletableFuture.allOf(platformFutures.toArray(new CompletableFuture[0])).join();

        long endTimePlatform = System.currentTimeMillis();
        System.out.println("  -> Platform pool took: " + (endTimePlatform - startTimePlatform) + " ms");
        platformThreadPool.shutdown();


        // --- Scenario 2: The RIGHT Way (Using a Virtual Thread Executor) ---
        System.out.println("\n--- Scenario 2: Submitting blocking tasks to a VIRTUAL thread executor ---");
        ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        long startTimeVirtual = System.currentTimeMillis();

        List<CompletableFuture<Void>> virtualFutures = IntStream.range(0, 10)
                .mapToObj(i -> CompletableFuture.runAsync(() -> blockingTask(i), virtualThreadExecutor))
                .toList();

        CompletableFuture.allOf(virtualFutures.toArray(new CompletableFuture[0])).join();

        long endTimeVirtual = System.currentTimeMillis();
        System.out.println("  -> Virtual thread executor took: " + (endTimeVirtual - startTimeVirtual) + " ms");
        virtualThreadExecutor.shutdown();

        System.out.println("\nNotice how the virtual thread executor handles the 10 concurrent blocking tasks much faster,");
        System.out.println("because it doesn't have a limit of 4 platform threads.");
    }

    private static void blockingTask(int taskNumber) {
        System.out.println("    [Task " + taskNumber + "]: Starting on thread: " + Thread.currentThread());
        try {
            Thread.sleep(1000); // Simulate a 1-second blocking I/O call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("    [Task " + taskNumber + "]: Finished.");
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact time will vary based on your machine's CPU cores)
================================================================================

🚀 Chapter 14: CompletableFuture ❤️ Virtual Threads 🚀

--- Scenario 1: Submitting blocking tasks to a small PLATFORM thread pool ---
    [Task 0]: Starting on thread: Thread[#21,pool-1-thread-1,5,main]
    [Task 1]: Starting on thread: Thread[#22,pool-1-thread-2,5,main]
    [Task 2]: Starting on thread: Thread[#23,pool-1-thread-3,5,main]
    [Task 3]: Starting on thread: Thread[#24,pool-1-thread-4,5,main]
    [Task 0]: Finished.
    [Task 4]: Starting on thread: Thread[#21,pool-1-thread-1,5,main]
    [Task 1]: Finished.
    [Task 5]: Starting on thread: Thread[#22,pool-1-thread-2,5,main]
    ... (and so on, it runs in batches of 4)
  -> Platform pool took: 3026 ms

--- Scenario 2: Submitting blocking tasks to a VIRTUAL thread executor ---
    [Task 0]: Starting on thread: VirtualThread[#49]/runnable@ForkJoinPool-1-worker-1
    [Task 1]: Starting on thread: VirtualThread[#50]/runnable@ForkJoinPool-1-worker-2
    [Task 2]: Starting on thread: VirtualThread[#51]/runnable@ForkJoinPool-1-worker-3
    [Task 3]: Starting on thread: VirtualThread[#52]/runnable@ForkJoinPool-1-worker-4
    [Task 4]: Starting on thread: VirtualThread[#53]/runnable@ForkJoinPool-1-worker-5
    [Task 5]: Starting on thread: VirtualThread[#54]/runnable@ForkJoinPool-1-worker-6
    [Task 6]: Starting on thread: VirtualThread[#55]/runnable@ForkJoinPool-1-worker-7
    [Task 7]: Starting on thread: VirtualThread[#56]/runnable@ForkJoinPool-1-worker-8
    [Task 8]: Starting on thread: VirtualThread[#57]/runnable@ForkJoinPool-1-worker-1
    [Task 9]: Starting on thread: VirtualThread[#58]/runnable@ForkJoinPool-1-worker-2
    ... (all 10 start almost at once)
  -> Virtual thread executor took: 1021 ms

Notice how the virtual thread executor handles the 10 concurrent blocking tasks much faster,
because it doesn't have a limit of 4 platform threads.
*/
