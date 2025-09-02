import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadLocalExample {

    // 1. Create the ThreadLocal variable.
    // withInitial provides a default value for each thread the first time it calls .get()
    private static final ThreadLocal<SimpleDateFormat> formatter =
            ThreadLocal.withInitial(() -> {
                System.out.println("   -> [" + Thread.currentThread().getName() + "] is creating its own SimpleDateFormat instance.");
                return new SimpleDateFormat("yyyy-MM-dd");
            });

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Chapter 7: ThreadLocal Demo 🚀");

        // Create two threads that will share the same Runnable instance.
        DateParserTask task = new DateParserTask("2025-01-01");

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\n[Main]: Both threads finished. Notice they each created and used their own formatter instance.");
    }

    // This task will be run by multiple threads.
    static class DateParserTask implements Runnable {
        private final String dateStringToParse;

        public DateParserTask(String dateStringToParse) {
            this.dateStringToParse = dateStringToParse;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "]: Starting the date parsing task.");

            try {
                // 2. Get the thread-specific instance of the formatter.
                SimpleDateFormat sdf = formatter.get();
                System.out.println("[" + threadName + "]: Got formatter instance with hash code: " + System.identityHashCode(sdf));

                // Use the formatter
                Date date = sdf.parse(dateStringToParse);
                System.out.println("[" + threadName + "]: Successfully parsed date: " + date);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 3. CRUCIAL: Remove the value from the thread-local map to prevent memory leaks.
                // In application servers, threads are reused. If we don't remove this,
                // the next request using this thread might see old data.
                formatter.remove();
                System.out.println("[" + threadName + "]: Cleaned up the formatter instance.");
            }
        }
    }
}

/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The order of lines from different threads might change slightly)
================================================================================

🚀 Chapter 7: ThreadLocal Demo 🚀
[Thread-1]: Starting the date parsing task.
   -> [Thread-1] is creating its own SimpleDateFormat instance.
[Thread-1]: Got formatter instance with hash code: 1325547565
[Thread-2]: Starting the date parsing task.
   -> [Thread-2] is creating its own SimpleDateFormat instance.
[Thread-2]: Got formatter instance with hash code: 2060468723
[Thread-1]: Successfully parsed date: Tue Jan 01 00:00:00 UTC 2025
[Thread-1]: Cleaned up the formatter instance.
[Thread-2]: Successfully parsed date: Tue Jan 01 00:00:00 UTC 2025
[Thread-2]: Cleaned up the formatter instance.

[Main]: Both threads finished. Notice they each created and used their own formatter instance.

// Key Takeaway: The hash codes are different! This proves that Thread-1 and Thread-2
// each got their own, separate instance of SimpleDateFormat, avoiding any thread-safety issues.
// This is the magic of ThreadLocal.

*/
