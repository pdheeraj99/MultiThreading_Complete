import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ee example lo manam `CopyOnWriteArrayList` yokka vinta (unique) behavior ni chuddam.
 *
 * Scenario:
 * - Manaki oka list of "listeners" or "subscribers" undi.
 * - Chala threads (event dispatchers) ee list lo unna prathi listener ki okesari
 *   oka event ni pampali anukuntunnayi (read/iterate).
 * - Appudappudu matrame, oka kotha listener add avuthadu or remove avuthadu (write).
 *
 * The Problem with `ArrayList`:
 * - `ArrayList` ni iterate chestunnapudu, inko thread daanini modify cheste (`add`/`remove`),
 *   ventane `ConcurrentModificationException` vastundi.
 *
 * The Solution: `CopyOnWriteArrayList`
 * - **Read:** Readers eppudu original, unchanged list meeda iterate chestayi. Vaatiki asalu lock eh undadu.
 * - **Write:** Writer vachinappudu, adi lopaliki velli, mottham list ni oka kotha copy create chesi,
 *   aa copy meeda change chesi, final ga aa kotha list ni point chestundi.
 *
 * Result:
 * - Readers eppudu block avvavu and vaatiki eppudu exception raadu.
 * - Oka writer modify chestunna, aa time lo already iterate avuthunna readers ki aah change kanapadadu.
 *   Vaallu paatha list ne chustaru (snapshot-like behavior). Kotha iteration start cheste, kotha list kanipistundi.
 * - Idi read-heavy scenarios ki chala powerful, kani writes chala costly.
 */
public class CopyOnWriteArrayListExample {

    public static void main(String[] args) throws InterruptedException {
        // Oka CopyOnWriteArrayList ni create cheddam
        List<String> listeners = new CopyOnWriteArrayList<>();
        listeners.add("Listener-A");
        listeners.add("Listener-B");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Oka reader ni start cheddam, adi continuously list ni read chestu untundi.
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            while (true) {
                System.out.printf("[%s] Reading listeners: %s\n", threadName, listeners);
                sleep(1000);
            }
        });

        // Inko reader, kani idi iterator vaduthundi.
        // Idi `CopyOnWriteArrayList` yokka snapshot behavior ni chala clear ga chupistundi.
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            while (true) {
                System.out.printf("[%s] Iterator tho start chestunnanu...\n", threadName);
                Iterator<String> iterator = listeners.iterator();
                while (iterator.hasNext()) {
                    // Iterator create ayinappudu unna list version ne chustundi.
                    // Madhyalo list maarina, ee iterator ki aah change teliyadu.
                    System.out.printf("  [%s] Notifying: %s\n", threadName, iterator.next());
                    sleep(500);
                }
                System.out.printf("[%s] ...Iterator tho poorthi ayyindi.\n", threadName);
                sleep(2000);
            }
        });

        // Ippudu, oka writer thread konchem sepu aagi, list ni modify chestundi.
        Thread writerThread = new Thread(() -> {
            sleep(2500);
            System.out.println("\n>>> [Writer] Kotha listener ni add chestunnanu... <<<");
            listeners.add("Listener-C");
            System.out.println(">>> [Writer] Listener-C add ayyindi. Current list: " + listeners + " <<<\n");

            sleep(3000);
            System.out.println("\n>>> [Writer] Listener-A ni remove chestunnanu... <<<");
            listeners.remove("Listener-A");
            System.out.println(">>> [Writer] Listener-A remove ayyindi. Current list: " + listeners + " <<<\n");
        });

        writerThread.start();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        executor.shutdownNow();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
