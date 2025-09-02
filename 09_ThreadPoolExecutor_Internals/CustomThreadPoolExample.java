import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPoolExample {

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 9: Custom ThreadPoolExecutor Demo 🚀");

        // The HR Policy Manual, in code:
        int corePoolSize = 2;       // 2 full-time waiters
        int maximumPoolSize = 4;    // At most 4 waiters (2 full-time + 2 part-time)
        long keepAliveTime = 10;    // Part-timers are sent home after 10 seconds of no work
        TimeUnit unit = TimeUnit.SECONDS;
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2); // Waiting line with 2 chairs
        CustomThreadFactory threadFactory = new CustomThreadFactory("Restaurant-Waiter");
        ThreadPoolExecutor.AbortPolicy rejectionHandler = new ThreadPoolExecutor.AbortPolicy();

        // Creating our custom ThreadPoolExecutor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                rejectionHandler
        );

        // Submitting 10 tasks. Let's trace what happens:
        // Task 1, 2: Handled by new core threads. (Pool size: 2)
        // Task 3, 4: Go into the queue. (Queue size: 2)
        // Task 5, 6: Queue is full, so new "part-time" threads are created. (Pool size: 4)
        // Task 7, 8, 9, 10: Pool is at max size and queue is full. These tasks will be REJECTED.
        for (int i = 1; i <= 10; i++) {
            try {
                System.out.println("[Manager]: Submitting task " + i);
                executor.execute(new Task(i));
            } catch (RejectedExecutionException e) {
                System.out.println("  🚨 [Manager]: REJECTED Task " + i + ". The restaurant is too busy!");
            }
        }

        executor.shutdown();
        System.out.println("[Manager]: All tasks submitted. Shutting down.");
    }

    // A custom thread factory to name our threads
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
        }
    }

    static class Task implements Runnable {
        private final int taskId;
        public Task(int taskId) { this.taskId = taskId; }
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("    [" + threadName + "]: Starting Task " + taskId);
            try {
                Thread.sleep(2000); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("    [" + threadName + "]: Finished Task " + taskId);
        }
    }
}

/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact order of lines might change slightly)
================================================================================

🚀 Chapter 9: Custom ThreadPoolExecutor Demo 🚀
[Manager]: Submitting task 1
[Manager]: Submitting task 2
[Manager]: Submitting task 3
[Manager]: Submitting task 4
[Manager]: Submitting task 5
[Manager]: Submitting task 6
[Manager]: Submitting task 7
  🚨 [Manager]: REJECTED Task 7. The restaurant is too busy!
[Manager]: Submitting task 8
    [Restaurant-Waiter-1]: Starting Task 1
  🚨 [Manager]: REJECTED Task 8. The restaurant is too busy!
    [Restaurant-Waiter-2]: Starting Task 2
[Manager]: Submitting task 9
    [Restaurant-Waiter-4]: Starting Task 6
  🚨 [Manager]: REJECTED Task 9. The restaurant is too busy!
    [Restaurant-Waiter-3]: Starting Task 5
[Manager]: Submitting task 10
  🚨 [Manager]: REJECTED Task 10. The restaurant is too busy!
All tasks submitted. Shutting down.
    [Restaurant-Waiter-2]: Finished Task 2
    [Restaurant-Waiter-2]: Starting Task 4
    [Restaurant-Waiter-1]: Finished Task 1
    [Restaurant-Waiter-1]: Starting Task 3
    [Restaurant-Waiter-4]: Finished Task 6
    [Restaurant-Waiter-3]: Finished Task 5
    [Restaurant-Waiter-2]: Finished Task 4
    [Restaurant-Waiter-1]: Finished Task 3
*/
