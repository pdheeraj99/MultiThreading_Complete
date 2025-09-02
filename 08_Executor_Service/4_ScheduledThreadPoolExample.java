import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Example4_ScheduledThreadPool {

    public static void main(String[] args) {
        System.out.println("--- 🚀 ScheduledThreadPool Demo ---");
        // Creating a pool that can schedule tasks.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        System.out.println("[Scheduler]: Submitting a task to run after a 3-second delay.");
        Runnable delayedTask = new Task(1);

        // Schedule the task to run once after an initial delay.
        scheduler.schedule(delayedTask, 3, TimeUnit.SECONDS);

        // To demonstrate scheduleAtFixedRate, you could use:
        // scheduler.scheduleAtFixedRate(delayedTask, 1, 5, TimeUnit.SECONDS);
        // This would run the task after 1 second, and then every 5 seconds.

        shutdownAndAwaitTermination(scheduler);
    }

    static class Task implements Runnable {
        private final int taskId;
        public Task(int taskId) { this.taskId = taskId; }
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("  [" + threadName + "]: Starting Task " + taskId + " at " + new java.util.Date());
            try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("  [" + threadName + "]: Finished Task " + taskId);
        }
    }

    static void shutdownAndAwaitTermination(ScheduledExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Pool has been shut down.");
    }
}
