/**
 * Ee example lo manam oka classic "Deadlock" ni ela create cheyalo chuddam.
 * Ee program run cheste, adi eppatiki aagadu (it will hang forever).
 *
 * Deenini meeru `jps` and `jstack` tools tho analyze cheyadaniki use cheskovachu.
 *
 * Scenario:
 * - Manaki rendu resources unnayi: a 'Pen' and a 'Paper'.
 * - Thread-A (Writer-1) ki rayadaniki mundu Pen, tarvata Paper kavali.
 * - Thread-B (Writer-2) ki rayadaniki mundu Paper, tarvata Pen kavali.
 *
 * The Deadlock Sequence:
 * 1. Writer-1 (Thread-A) `Pen` lock ni teeskuntundi.
 * 2. Ade samayam lo, Writer-2 (Thread-B) `Paper` lock ni teeskuntundi.
 * 3. Ippudu, Writer-1 `Paper` lock kosam try chestundi. Kani adi Writer-2 daggara undi. So, Writer-1 waits.
 * 4. Ade samayam lo, Writer-2 `Pen` lock kosam try chestundi. Kani adi Writer-1 daggara undi. So, Writer-2 waits.
 *
 * Iddariki iddaru, okari daggara unna resource kosam inkokaru wait chestu, anantham ga undipotaru.
 * This is a Deadlock.
 */
public class DeadlockExample {

    // Rendu shared resources, veetini manam locks ga vadatham.
    private static final Object pen = new Object();
    private static final Object paper = new Object();

    public static void main(String[] args) {
        System.out.println("Deadlock scenario start avabothondi...");

        // Thread-A: Mundu Pen, tarvata Paper
        Thread writer1 = new Thread(() -> {
            synchronized (pen) {
                System.out.println("Writer-1: Pen teeskunnanu. Paper kosam trying...");
                sleep(100); // Inko thread ki chance ivvadaniki chinna gap

                synchronized (paper) {
                    System.out.println("Writer-1: Paper kuda teeskunnanu. Rayadam modalettanu.");
                }
            }
        }, "Writer-1-Thread-A");

        // Thread-B: Mundu Paper, tarvata Pen (Locking order reverse chesam)
        Thread writer2 = new Thread(() -> {
            synchronized (paper) {
                System.out.println("Writer-2: Paper teeskunnanu. Pen kosam trying...");
                sleep(100);

                synchronized (pen) {
                    System.out.println("Writer-2: Pen kuda teeskunnanu. Rayadam modalettanu.");
                }
            }
        }, "Writer-2-Thread-B");

        writer1.start();
        writer2.start();

        System.out.println("\nEe program hang avuthundi. Manual ga stop cheyali (Ctrl+C).");
        System.out.println("Ee time lo, terminal lo `jps -l` and `jstack <PID>` commands use chesi analyze cheyochu.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
