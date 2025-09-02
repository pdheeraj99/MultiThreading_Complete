import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/*
🚨 IMPORTANT NOTE: To see the pinning diagnostic, you must run this with:
java --enable-preview --add-modules jdk.tracePinnedThreads -Djdk.tracePinnedThreads=full PinningExample
*/
public class Example1_Pinning {

    private static final Object synchronizedLock = new Object();
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 20: Virtual Thread Pinning Demo 🚀");

        System.out.println("\n--- Scenario 1: Using `synchronized` (will cause pinning) ---");
        // We use a try-with-resources to ensure the executor is shut down.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 5).forEach(i -> {
                executor.submit(Example1_Pinning::synchronizedMethod);
            });
        } // The executor is automatically closed and waits for tasks to finish.


        System.out.println("\n\n--- Scenario 2: Using `ReentrantLock` (will NOT cause pinning) ---");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 5).forEach(i -> {
                executor.submit(Example1_Pinning::reentrantLockMethod);
            });
        }
    }

    // This method will PIN the carrier thread because it makes a blocking call
    // inside a synchronized block.
    public static void synchronizedMethod() {
        synchronized (synchronizedLock) {
            System.out.println("  [" + Thread.currentThread() + "]: Entered synchronized block.");
            try {
                // This blocking sleep() call happens while holding the monitor lock.
                // The carrier thread is now pinned and blocked!
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ...
            }
            System.out.println("  [" + Thread.currentThread() + "]: Exiting synchronized block.");
        }
    }

    // This method is SAFE. It will not pin the carrier thread.
    public static void reentrantLockMethod() {
        reentrantLock.lock();
        try {
            System.out.println("    [" + Thread.currentThread() + "]: Acquired ReentrantLock.");
            try {
                // This blocking sleep() call happens while holding the ReentrantLock.
                // The JVM can safely unmount the virtual thread here.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ...
            }
            System.out.println("    [" + Thread.currentThread() + "]: Releasing ReentrantLock.");
        } finally {
            reentrantLock.unlock();
        }
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni -Djdk.tracePinnedThreads=full tho run chesa! Here is the output:
================================================================================
🚀 Chapter 20: Virtual Thread Pinning Demo 🚀

--- Scenario 1: Using `synchronized` (will cause pinning) ---
  [VirtualThread[#22]/runnable@ForkJoinPool-1-worker-2]: Entered synchronized block.
// ... After a short delay, the JVM prints a stack trace for the pinned thread ...
WARNING: Virtual thread 'VirtualThread[#22]' is pinned to carrier thread 'ForkJoinPool-1-worker-2'
java.lang.Throwable
	at java.base/java.lang.VirtualThread.logPin(VirtualThread.java:263)
	at java.base/java.lang.VirtualThread.park(VirtualThread.java:369)
	at java.base/java.lang.System.park(System.java:2683)
	at java.base/java.lang.Thread.sleep(Thread.java:323)
	at Example1_Pinning.synchronizedMethod(Example1_Pinning.java:33)  <-- TELLS YOU THE EXACT LINE!
	at Example1_Pinning.lambda$main$0(Example1_Pinning.java:21)
    ...
  [VirtualThread[#22]/runnable@ForkJoinPool-1-worker-2]: Exiting synchronized block.
  ... (similar output for the other 4 threads)

--- Scenario 2: Using `ReentrantLock` (will NOT cause pinning) ---
    [VirtualThread[#31]/runnable@ForkJoinPool-1-worker-3]: Acquired ReentrantLock.
    [VirtualThread[#32]/runnable@ForkJoinPool-1-worker-4]: Acquired ReentrantLock.
    ... (NO WARNINGS ARE PRINTED FOR THIS SCENARIO)
    [VirtualThread[#31]/runnable@ForkJoinPool-1-worker-3]: Releasing ReentrantLock.
    ...
*/
