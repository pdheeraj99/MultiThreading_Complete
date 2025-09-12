import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Ee example lo manam Java lo threads ela create cheyalo nerchukuntam.
 * We will explore three main ways:
 * 1. Implementing the Runnable interface (Recommended).
 * 2. Extending the Thread class.
 * 3. Using a Callable to get a return value.
 */
public class ThreadCreationExample {

    public static void main(String[] args) throws Exception {
        System.out.println("Main thread modalaindi! Current thread: " + Thread.currentThread().getName());

        // --- విధానం 1: Runnable Interface (The Best Way) ---
        // Manam oka task ni create chesi, daanini Thread object ki istham.
        // Task veru, thread veru. Idi మంచి design.
        System.out.println("\n--- 1. Runnable tho Thread ni Create cheyadam ---");
        PdfReportGenerator pdfTask = new PdfReportGenerator();
        Thread pdfThread = new Thread(pdfTask, "Pdf-Generator-Thread");
        pdfThread.start(); // Ekkada kottha thread start avuthundi.

        // --- విధానం 2: Thread Class ni Extend cheyadam ---
        // Direct ga Thread class ne extend chestunnam.
        System.out.println("\n--- 2. Thread class ni Extend cheyadam ---");
        ExcelReportGenerator excelThread = new ExcelReportGenerator();
        excelThread.setName("Excel-Generator-Thread");
        excelThread.start();

        // --- విధానం 3: Callable & FutureTask (To get a result back) ---
        // Ee task manaki oka result (String) return chestundi.
        // Daani kosam manam FutureTask use chesi result ni capture cheddam.
        System.out.println("\n--- 3. Callable tho Thread ni Create cheyadam (oka value return cheyadaniki) ---");
        DataAggregator dataTask = new DataAggregator("SalesData");
        FutureTask<String> futureTask = new FutureTask<>(dataTask);
        Thread dataThread = new Thread(futureTask, "Data-Aggregator-Thread");
        dataThread.start();

        // --- Daemon Thread Example ---
        // Idi oka background monitoring task anukundam.
        // Main thread aipogane, deeni pani kuda aagipovali.
        System.out.println("\n--- Daemon Thread Example ---");
        Thread monitorThread = new Thread(() -> {
            while (true) {
                System.out.println("[Monitor] System health check...");
                try {
                    Thread.sleep(2000); // প্রতি 2 seconds ki check chestundi
                } catch (InterruptedException e) {
                    // Daemon threads often run in infinite loops, so interruption is a way to stop them.
                    System.out.println("[Monitor] Nannu aapesaru!");
                    break;
                }
            }
        }, "System-Monitor-Daemon");

        monitorThread.setDaemon(true); // Deenini daemon ga set chestunnam. Idi chala important!
        monitorThread.start();

        // Ippudu, manam data aggregator nunchi result kosam wait cheddam.
        // futureTask.get() anedi blocking call. Ante, result vachhe varaku aagutundi.
        String aggregationResult = futureTask.get();
        System.out.println("\n[Main] Callable nunchi vachina result: " + aggregationResult);


        // Main thread konchem pani chesaka aagipotundi.
        System.out.println("\nMain thread pani complete avvabothondi...");
        Thread.sleep(1000);
        System.out.println("Main thread aipoyindi! Ippudu Daemon thread kuda aagipovali choodu.");
    }

    // విధానం 1: Runnable Interface
    static class PdfReportGenerator implements Runnable {
        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            System.out.println("[" + currentThreadName + "] PDF Report generate cheyadam modalaindi...");
            try {
                // Simulating some work
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[" + currentThreadName + "] PDF Report ready ayyindi!");
        }
    }

    // విధానం 2: Thread Class
    static class ExcelReportGenerator extends Thread {
        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            System.out.println("[" + currentThreadName + "] Excel Report generate cheyadam modalaindi...");
            try {
                // Simulating some work
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[" + currentThreadName + "] Excel Report ready ayyindi!");
        }
    }

    // విధానం 3: Callable Interface
    static class DataAggregator implements Callable<String> {
        private final String source;

        public DataAggregator(String source) {
            this.source = source;
        }

        @Override
        public String call() throws Exception {
            String currentThreadName = Thread.currentThread().getName();
            System.out.println("[" + currentThreadName + "] Data aggregation from '" + source + "' modalaindi...");
            // Simulating a long-running task that computes a value
            Thread.sleep(4000);
            System.out.println("[" + currentThreadName + "] Data aggregation poorti ayyindi.");
            return "Total Records Aggregated: " + 42; // Oka dummy result ni return chestunnam
        }
    }
}
