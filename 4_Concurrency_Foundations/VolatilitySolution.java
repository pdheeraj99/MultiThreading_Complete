/**
 * Ee example lo manam `volatile` keyword tho `VisibilityProblem` ni ela solve cheyalo chuddam.
 *
 * Manam appudu chusina `VisibilityProblem.java` lo, reader thread anedi writer thread
 * chesina change (`isDataReady = true`) ni chudaleka infinite loop lo undipoindi.
 *
 * The Solution:
 * - Ippudu manam `isDataReady` variable ni `volatile` ga declare chestam.
 * - `volatile` anedi JVM ki cheptundi: "Eey, ee variable chala important. Deeni meeda
 *   jarige prathi read and write direct ga main memory nunchi cheyali. CPU Caches tho
 *   gimmicks vaddu!"
 *
 * Result:
 * - Writer thread `isDataReady` ni `true` ga marchagane, aa change ventane main memory loki veltundi.
 * - Reader thread prathi sari main memory nunchi ee variable ni check chestundi kabatti,
 *   daaniki ee kotha `true` value ventane kanipistundi, and loop break avuthundi.
 * - The program now terminates correctly!
 */
public class VolatilitySolution {

    // Ekkada choodandi, 'volatile' ane magic word add chesam.
    private static volatile boolean isDataReady = false;

    public static void main(String[] args) {
        // Reader Thread
        Thread readerThread = new Thread(() -> {
            System.out.println("Reader thread: Data kosam waiting...");
            // `volatile` valla, ee loop ippudu break avuthundi.
            while (!isDataReady) {
                // Spinning...
            }
            System.out.println("Reader thread: Data ready ayyindi! Pani modalupedutunna.");
        }, "ReaderThread");

        // Writer Thread
        Thread writerThread = new Thread(() -> {
            System.out.println("Writer thread: Data prepare chestunnanu...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isDataReady = true;
            System.out.println("Writer thread: Volatile flag ni `true` ga set chesa.");
        }, "WriterThread");

        readerThread.start();
        writerThread.start();
    }
}
