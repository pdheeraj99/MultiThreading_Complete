import java.util.concurrent.CountDownLatch;

/**
 * Ee example lo manam `CountDownLatch` ni ela vadalo nerchukuntam.
 *
 * Scenario:
 * - Oka server application undi. Adi start avvadaniki mundu, daaniki kavalsina
 *   konni critical services (Database, Cache, Messaging) antha ready ga undali.
 * - Ee services anni parallel ga start avvochu, time pattొచ్చు.
 * - Main application thread anedi ee services anni start ayye varaku wait cheyali.
 *   Anni ready ayyaka matrame, "Server is UP and Running" ani cheppali.
 *
 * Solution: `CountDownLatch`
 * - Manam oka `CountDownLatch` ni 3 (number of services) ane count tho create cheddam.
 * - Prathi service tana startup pani poorthi cheskuni, `latch.countDown()` ni pilustundi.
 * - Main thread `latch.await()` ani pilichi, count zero ayye varaku wait chestundi.
 * - Count zero avvagane, `await()` nunchi bayataki vachi, server ni start chestundi.
 */
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        // 3 services unnay kabatti, count ni 3 ga set cheddam.
        CountDownLatch latch = new CountDownLatch(3);

        System.out.println("Server startup process modalaindi...");

        // Services ni separate threads lo start cheddam.
        Thread dbService = new Thread(new Service("DatabaseService", 2000, latch), "DB-Thread");
        Thread cacheService = new Thread(new Service("CacheService", 3000, latch), "Cache-Thread");
        Thread messagingService = new Thread(new Service("MessagingService", 4000, latch), "Messaging-Thread");

        dbService.start();
        cacheService.start();
        messagingService.start();

        // Ippudu main thread ee gate (latch) daggara wait chestundi.
        // Count zero ayye varaku `await()` method block avuthundi.
        System.out.println("Main thread: Anni services start ayye varaku waiting...");
        latch.await();

        // Ee line execute avuthondi ante, count zero aipoindi ani அர்த்தం!
        // Ante, anni services ready ga unnayi.
        System.out.println("=====================================================");
        System.out.println("Main thread: Anni services ready! Server is UP and Running!");
        System.out.println("=====================================================");
    }

    // Ee class oka generic service ni represent chestundi.
    static class Service implements Runnable {
        private final String name;
        private final int startupTime;
        private final CountDownLatch latch;

        public Service(String name, int startupTime, CountDownLatch latch) {
            this.name = name;
            this.startupTime = startupTime;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                System.out.printf("[%s] startup modalaindi...\n", name);
                Thread.sleep(startupTime); // Startup time ni simulate cheddam
                System.out.printf("[%s] UP and Ready.\n", name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Tana pani aipoyindi, so latch count ni okati taggistundi.
                System.out.printf("[%s] latch ni countdown chestondi.\n", name);
                latch.countDown();
            }
        }
    }
}
