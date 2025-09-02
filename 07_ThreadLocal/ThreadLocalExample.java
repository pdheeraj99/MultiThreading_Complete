import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalExample {

    // 1. Create the ThreadLocal variable using withInitial for lazy initialization.
    private static final ThreadLocal<SimpleDateFormat> formatter =
            ThreadLocal.withInitial(() -> {
                System.out.println("   -> [" + Thread.currentThread().getName() + "] is creating its own SimpleDateFormat instance.");
                return new SimpleDateFormat("yyyy-MM-dd");
            });

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 7: ThreadLocal Demo 🚀");

        // Use an executor to simulate a server environment where threads are reused.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit tasks for two different "users" or "requests".
        executor.submit(new DateParserTask("2025-01-01", "User-A"));
        executor.submit(new DateParserTask("2025-08-15", "User-B"));

        // Let the first batch finish
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        System.out.println("\n--- Submitting more tasks to the same pool, threads will be reused ---");
        // These tasks will likely run on the same threads as before.
        executor.submit(new DateParserTask("2026-03-10", "User-C"));
        executor.submit(new DateParserTask("2026-11-20", "User-D"));

        executor.shutdown();
    }

    static class DateParserTask implements Runnable {
        private final String dateStringToParse;
        private final String user;

        public DateParserTask(String dateStringToParse, String user) {
            this.dateStringToParse = dateStringToParse;
            this.user = user;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "]: Handling request for " + user);

            try {
                // 2. Get the thread-specific instance of the formatter.
                SimpleDateFormat sdf = formatter.get();
                System.out.println("  [" + threadName + "]: Got formatter instance with hash code: " + System.identityHashCode(sdf));

                Date date = sdf.parse(dateStringToParse);
                System.out.println("  [" + threadName + "]: Successfully parsed date: " + date + " for " + user);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 3. CRUCIAL: Remove the value to prevent memory/logic leaks in a pooled environment.
                // If you comment this out, the next task on this thread will see the *same* formatter instance.
                formatter.remove();
                System.out.println("  [" + threadName + "]: Cleaned up the formatter instance for " + user);
            }
        }
    }
}
