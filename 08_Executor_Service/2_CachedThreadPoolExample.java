import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example2_CachedThreadPool {

    public static void main(String[] args) {
        System.out.println("--- 🚀 CachedThreadPool Demo ---");
        // Creating a pool that creates new threads as needed.
        ExecutorService executor = Executors.newCachedThreadPool();

        // Submit 5 tasks. It will likely create 5 new threads for this burst.
        for (int i = 1; i <= 5; i++) {
            executor.submit(new Task(i));
        }

        shutdownAndAwaitTermination(executor);
    }

    static class Task implements Runnable {
        private final int taskId;
        public Task(int taskId) { this.taskId = taskId; }
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("  [" + threadName + "]: Starting Task " + taskId);
            try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("  [" + threadName + "]: Finished Task " + taskId);
        }
    }

    static void shutdownAndAwaitTermination(ExecutorService pool) {
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
