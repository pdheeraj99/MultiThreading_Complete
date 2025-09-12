import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ee example lo manam `ExecutorService` and Thread Pools valla vache advantages ento chuddam.
 *
 * Scenario:
 * - Manam 100 photos ni process cheyali anukundam.
 * - Manual ga chesthe, 100 sarlu `new Thread().start()` analsi vastundi. Idi chala inefficient and dangerous.
 *
 * Solution: `ExecutorService`
 * - Manam oka fixed thread pool (Ex: 4 threads tho) create cheddam.
 * - Aa 100 tasks ni ee pool ki submit cheddam.
 * - The ExecutorService will cleverly manage these 4 threads to complete all 100 tasks without creating 100 threads.
 */
public class ExecutorFrameworkExample {

    public static void main(String[] args) {
        System.out.println("Main thread modalaindi.");

        // Step 1: Oka Fixed Thread Pool ni create cheddam.
        // Ee pool lo eppudu 4 threads matrame untayi.
        // Ee number ni manam CPU cores batti decide cheskovachu (Ex: Runtime.getRuntime().availableProcessors())
        int numberOfThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        System.out.println("Oka Fixed Thread Pool (4 threads) create cheyabadindi.");
        System.out.println("100 photo processing tasks ni submit chestunnam...");

        // Step 2: Tasks ni create chesi, pool ki submit cheyali.
        for (int i = 1; i <= 100; i++) {
            Runnable task = new PhotoProcessor("Photo-" + i);
            executor.submit(task);
        }

        // Step 3: Pani aipoyaka ExecutorService ni shutdown cheyadam chala important!
        // `shutdown()` anedi kotha tasks ni teeskodu, kani unna tasks ni complete cheyadaniki allow chestundi.
        executor.shutdown();
        System.out.println("Executor shutdown cheyabadindi. Kotha tasks accept cheyadu.");

        try {
            // Optional: Main thread anedi anni tasks poorthi ayye varaku wait cheyadaniki.
            // Manam oka timeout istham. Aa time loపు tasks aipokapothe, `awaitTermination` false istundi.
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Anni tasks time loపు complete avvaledu. Forcing shutdown...");
                executor.shutdownNow(); // Running tasks ni interrupt cheyadaniki try chestundi.
            }
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted while waiting.");
            executor.shutdownNow();
        }

        System.out.println("Main thread: Anni tasks poorthi ayyayi. Program mugisindi.");
    }

    // Ee task oka photo ni process chesinattu simulate chestundi.
    static class PhotoProcessor implements Runnable {
        private final String photoName;

        public PhotoProcessor(String photoName) {
            this.photoName = photoName;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.printf("[%s] '%s' ni process cheyadam modalaindi...\n", threadName, photoName);
            try {
                // Processing time ni simulate cheddam
                Thread.sleep((long) (Math.random() * 100));
            } catch (InterruptedException e) {
                // shutdownNow() pilichinappudu, ee exception raavachu.
                System.out.printf("[%s] was interrupted while processing '%s'.\n", threadName, photoName);
                Thread.currentThread().interrupt(); // Interrupt status ni preserve cheyadam manchi practice.
            }
            // System.out.printf("[%s] '%s' processing poorthi ayyindi.\n", threadName, photoName);
        }
    }
}
