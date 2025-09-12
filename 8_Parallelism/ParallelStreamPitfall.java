import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ee example lo manam `parallelStream()` ni blocking I/O operations tho vadithe
 * vache pedda samasya (pitfall) ento chuddam.
 *
 * Scenario:
 * - Manaki konni website URLs unnay anukundam. Vaati content ni download cheyali.
 * - Ee download anedi oka blocking I/O operation (network call).
 * - "Parallel ga chesthe vegamga avuthundi kada?" ani manam `parallelStream()` vadudam.
 *
 * The Problem:
 * - `parallelStream()` anedi background lo common `ForkJoinPool` ni vaduthundi.
 * - Ee pool lo unna threads sankhya (number of threads) anedi mee CPU cores batti untundi (Ex: 4, 8, 16). Idi limit ga untundi.
 * - Manam `fetchURL()` (blocking call) ni parallel stream lo run chesinappudu, aa common pool lo unna threads anni
 *   ee network call poorthi ayye varaku block aipotayi.
 *
 * - Imagine, mee daggara 4 worker threads unnay anukondi.
 *   - Modati 4 URLs ni ee 4 threads teeskuni, network call cheyadam start chestayi.
 *   - Ippudu ee 4 threads block aipoyayi! Vere panulu cheyadaniki inka threads levu.
 *   - 5va URL process avvali ante, ee 4 lo edoka thread free avvali.
 *
 * Result:
 * - Manam anukunna parallelism asalu jaragadu. System antha slow aipothundi.
 * - Inka darunam entante, mee application lo vere chota kuda evaraina `parallelStream()` vaduthunte,
 *   vaalla panulu kuda aagipotayi, endukante manam common pool ni antha block chesi pettesam!
 *
 * Golden Rule: NEVER run blocking I/O operations inside a `parallelStream()`.
 * I/O kosam eppudu `ExecutorService` with a dedicated thread pool vadali.
 */
public class ParallelStreamPitfall {

    public static void main(String[] args) {
        // 12 dummy URLs create cheddam.
        List<String> urls = IntStream.rangeClosed(1, 12)
                .mapToObj(i -> "http://api.example.com/data/" + i)
                .collect(Collectors.toList());

        System.out.println("Common ForkJoinPool lo unna threads sankhya (approx): " +
                Runtime.getRuntime().availableProcessors());
        System.out.println("12 blocking tasks ni parallelStream tho start chestunnam...");

        long startTime = System.currentTimeMillis();

        // Ee code chala dangerous!
        urls.parallelStream().forEach(ParallelStreamPitfall::fetchURL);

        long endTime = System.currentTimeMillis();

        System.out.println("\nPani poorthi ayyindi.");
        System.out.println("Total time taken: " + (endTime - startTime) + " ms");
        System.out.println("Chusara? 12 tasks unna, time taken is high because only a few threads did all the work sequentially in batches.");
    }

    // Ee method oka network call ni simulate chestundi. Idi oka blocking operation.
    public static void fetchURL(String url) {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] '%s' ni fetch cheyadam modalaindi...\n", threadName, url);
        try {
            // Blocking I/O call simulation
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("[%s] '%s' fetch cheyadam poorthi ayyindi.\n", threadName, url);
    }
}
