import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ee example lo manam Virtual Threads yokka power ento chuddam.
 *
 * Scenario:
 * - Manam 100,000 I/O-bound tasks ni okesari run cheyali anukundam.
 * - Prathi task oka network call chesi, 1 second wait chestundi anukundam.
 *
 * The Challenge with Platform Threads:
 * - Manam `Executors.newFixedThreadPool(100000)` ani create cheyadaniki try cheste,
 *   mee system 100,000 OS threads ni create cheyaleka, `OutOfMemoryError` tho crash aipothundi.
 *   Platform threads anevi chala costly resource.
 *
 * The Solution with Virtual Threads:
 * - Virtual threads chala lightweight. Manam vaatini lakshalalo create cheyochu.
 * - `Executors.newVirtualThreadPerTaskExecutor()` anedi prathi task ki oka kotha virtual thread ni create chestundi.
 * - Ee 100,000 virtual threads anni, background lo konni (Ex: 8 or 16) platform (carrier) threads meeda run avuthayi.
 * - Oka virtual thread `Thread.sleep()` chesinappudu, adi carrier thread nunchi unmount avuthundi,
 *   so aa carrier thread vere virtual thread ni run cheyadaniki free avuthundi.
 *
 * Result:
 * - Ee program `OutOfMemoryError` lekunda, chala easy ga run avuthundi.
 * - Anni 100,000 tasks kuda almost okate sari (konchem time difference tho) poorthi avuthayi.
 *   This is the magic of virtual threads for I/O-bound workloads!
 *
 * NOTE: Ee code run cheyalante, meeku JDK 21 or higher version kavali.
 */
public class VirtualThreadsExample {

    public static void main(String[] args) throws InterruptedException {
        int numberOfTasks = 100_000;
        System.out.printf("%d I/O-bound tasks ni virtual threads tho start chestunnam...\n", numberOfTasks);

        long startTime = System.currentTimeMillis();

        // Prathi task ki oka kotha virtual thread ni create chese special executor.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numberOfTasks; i++) {
                int taskNumber = i + 1;
                executor.submit(() -> {
                    // Blocking I/O operation ni simulate cheddam
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (taskNumber % 10000 == 0) { // Prathi 10,000 tasks ki oka message print cheddam
                        System.out.printf("Task %d poorthi ayyindi.\n", taskNumber);
                    }
                });
            }
            // `try-with-resources` block valla, `executor.close()` (which calls shutdown and waits)
            // automatically call avuthundi.
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("\nAnni %d tasks poorthi ayyayi.\n", numberOfTasks);
        System.out.printf("Total time taken: %d ms\n", (endTime - startTime));
        System.out.println("Chusara? Anni tasks almost 1 second lo ne poorthi ayyayi, endukante anni parallel ga run ayyayi!");
    }
}
