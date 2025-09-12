import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ee example lo manam `ConcurrentHashMap` yokka power ento chuddam.
 *
 * Scenario:
 * - Manam oka cache (or a shared map) ni maintain chestunnam.
 * - Multiple threads okesari ee map loki data ni raayadaniki (put) try chestayi.
 *
 * The Problem with `HashMap` or `synchronizedMap`:
 * - `HashMap` ni direct ga vadithe, `ConcurrentModificationException` ravochu or data loss avvochu.
 * - `Collections.synchronizedMap(new HashMap<>())` vadithe, adi safe eh, kani chala slow.
 *   Endukante, mottham map ki okate lock untundi. Oka thread write chestunte, vere threads
 *   read cheyadaniki kuda wait cheyali.
 *
 * The Solution: `ConcurrentHashMap`
 * - Idi high concurrency kosam design chesaru.
 * - Idi internal ga multiple locks (lock striping) vaduthundi, so multiple threads
 *   okesari, map lo unna different parts ni safely access cheyagalavu.
 * - Result: High performance and thread safety, without manual locking.
 */
public class ConcurrentHashMapExample {

    public static void main(String[] args) throws InterruptedException {
        // Oka ConcurrentHashMap ni create cheddam.
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();

        // Test kosam, 5 threads ni create cheddam.
        ExecutorService executor = Executors.newFixedThreadPool(5);

        int tasksPerThread = 1000;
        int numberOfThreads = 5;

        System.out.printf("%d threads, prathi thread %d items ni map lo peduthundi...\n", numberOfThreads, tasksPerThread);

        for (int i = 0; i < numberOfThreads; i++) {
            int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < tasksPerThread; j++) {
                    String key = "Thread-" + threadId + "-Key-" + j;
                    concurrentMap.put(key, j);
                }
            });
        }

        // Executor ni shutdown chesi, anni tasks complete ayye varaku wait cheddam.
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Final map size ni check cheddam.
        int expectedSize = numberOfThreads * tasksPerThread;
        System.out.println("\nFinal Map Size: " + concurrentMap.size());
        System.out.println("Expected Map Size: " + expectedSize);

        if (concurrentMap.size() == expectedSize) {
            System.out.println("Success! `ConcurrentHashMap` data ni correctly and safely handle chesindi.");
        } else {
            System.out.println("Failure! Data loss jarigindi.");
        }

        // Note: Ide pani normal HashMap tho cheste, exception ravochu or size taggipovachu.
    }
}
