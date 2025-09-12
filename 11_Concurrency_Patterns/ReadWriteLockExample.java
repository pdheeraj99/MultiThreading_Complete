import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Ee example lo manam Reader-Writer Lock pattern ni `ReadWriteLock` tho ela implement cheyalo chuddam.
 *
 * Scenario:
 * - Manaki oka "Application Configuration" map undi. Ee configuration ni chala sarlu
 *   chala threads read chestayi. Kani, appudappudu matrame oka admin thread daanini update chestundi.
 *
 * The Problem with a simple `ReentrantLock`:
 * - Manam okate lock vadithe, oka thread read chestunna, inko thread read cheyaleka wait cheyali.
 *   Idi performance ni taggistundi, endukante reads anevi harmless.
 *
 * The Solution: `ReadWriteLock`
 * - `ReadWriteLock` manaki rendu locks istundi: `readLock` and `writeLock`.
 * - Read operations kosam, manam `readLock` vadatham. Multiple threads okesari `readLock` ni teeskoni,
 *   data ni parallel ga chadavochu.
 * - Write operations kosam, manam `writeLock` vadatham. Idi exclusive lock. Okate sari okka thread matrame
 *   `writeLock` ni teeskogaladu. Adi unnapudu, vere readers or writers evaru lopaliki raleru.
 */
public class ReadWriteLockExample {

    public static void main(String[] args) {
        SharedConfig config = new SharedConfig();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Mugguru reader threads ni submit cheddam
        executor.submit(new Reader(config, "Reader-1"));
        executor.submit(new Reader(config, "Reader-2"));
        executor.submit(new Reader(config, "Reader-3"));

        // Oka writer thread ni submit cheddam
        executor.submit(new Writer(config, "Admin-Writer"));

        // Malli konni reader threads ni submit cheddam
        executor.submit(new Reader(config, "Reader-4"));
        executor.submit(new Reader(config, "Reader-5"));

        executor.shutdown();
    }

    static class SharedConfig {
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock readLock = rwLock.readLock();
        private final Lock writeLock = rwLock.writeLock();
        private final Map<String, String> configuration = new HashMap<>();

        public SharedConfig() {
            // Initial configuration
            configuration.put("theme", "dark");
            configuration.put("font-size", "14px");
        }

        // Read operation kosam readLock vadali
        public String get(String key) {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " reading config...");
                sleep(1000); // Read cheyadaniki time paduthunattu simulate cheddam
                return configuration.get(key);
            } finally {
                System.out.println(Thread.currentThread().getName() + " finished reading.");
                readLock.unlock();
            }
        }

        // Write operation kosam writeLock vadali
        public void set(String key, String value) {
            writeLock.lock();
            try {
                System.out.println(">>> " + Thread.currentThread().getName() + " WRITING config... Everyone else must wait! <<<");
                sleep(2000); // Write cheyadaniki time paduthunattu simulate cheddam
                configuration.put(key, value);
            } finally {
                System.out.println(">>> " + Thread.currentThread().getName() + " finished WRITING. <<<");
                writeLock.unlock();
            }
        }

        private static void sleep(int millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Reader implements Runnable {
        private final SharedConfig config;
        private final String name;

        public Reader(SharedConfig config, String name) {
            this.config = config;
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);
            String theme = config.get("theme");
            // System.out.println(name + " saw theme: " + theme);
        }
    }

    static class Writer implements Runnable {
        private final SharedConfig config;
        private final String name;

        public Writer(SharedConfig config, String name) {
            this.config = config;
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);
            config.set("font-size", "16px");
        }
    }
}
