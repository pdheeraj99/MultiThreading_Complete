import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example1_VirtualThreads {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Chapter 13: Virtual Threads Demo 🚀");

        // We will create and start 1,000,000 threads.
        // If you change ofVirtual() to ofPlatform(), this code will crash with an OutOfMemoryError.
        // But with virtual threads, it runs easily.

        System.out.println("[Main]: Creating a Virtual-Thread-Per-Task Executor.");
        // This is the new, modern way to create an executor for virtual threads.
        // It creates a new virtual thread for each submitted task.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            System.out.println("[Main]: Submitting 1,000,000 simple tasks...");
            for (int i = 0; i < 1_000_000; i++) {
                int taskNumber = i;
                executor.submit(() -> {
                    // Each task sleeps for 1 second. This is a blocking operation.
                    // With platform threads, this would require 1,000,000 OS threads, which is impossible.
                    // With virtual threads, the JVM will unmount the thread from the carrier
                    // while it's sleeping, freeing up the OS thread for other work.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (taskNumber % 100_000 == 0) {
                       System.out.println("  Task " + taskNumber + " finished on thread: " + Thread.currentThread());
                    }
                });
            }
            System.out.println("[Main]: All tasks submitted.");
            // The try-with-resources block will automatically call executor.shutdown() and wait for completion.
        }
        // The executor.close() method (called by try-with-resources) waits for all threads to finish.

        System.out.println("[Main]: All 1,000,000 tasks have completed successfully. This would be impossible with platform threads.");
    }
}

/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (This will take a moment to run)
================================================================================

🚀 Chapter 13: Virtual Threads Demo 🚀
[Main]: Creating a Virtual-Thread-Per-Task Executor.
[Main]: Submitting 1,000,000 simple tasks...
[Main]: All tasks submitted.
  Task 0 finished on thread: VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
  Task 900000 finished on thread: VirtualThread[#900022]/runnable@ForkJoinPool-1-worker-8
  Task 100000 finished on thread: VirtualThread[#100022]/runnable@ForkJoinPool-1-worker-4
  Task 200000 finished on thread: VirtualThread[#200022]/runnable@ForkJoinPool-1-worker-6
  Task 300000 finished on thread: VirtualThread[#300022]/runnable@ForkJoinPool-1-worker-8
  Task 400000 finished on thread: VirtualThread[#400022]/runnable@ForkJoinPool-1-worker-6
  Task 500000 finished on thread: VirtualThread[#500022]/runnable@ForkJoinPool-1-worker-4
  Task 600000 finished on thread: VirtualThread[#600022]/runnable@ForkJoinPool-1-worker-2
  Task 700000 finished on thread: VirtualThread[#700022]/runnable@ForkJoinPool-1-worker-1
  Task 800000 finished on thread: VirtualThread[#800022]/runnable@ForkJoinPool-1-worker-3
[Main]: All 1,000,000 tasks have completed successfully. This would be impossible with platform threads.

// Key Takeaway: Notice how all these virtual threads are running on a small number of
// ForkJoinPool worker threads (the "carrier" platform threads). We achieved massive
// concurrency with very few OS resources.

*/
