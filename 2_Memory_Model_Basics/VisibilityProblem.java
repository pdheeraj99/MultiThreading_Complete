/**
 * Ee example JMM lo "Visibility Problem" ni చూపిస్తుంది.
 *
 * Scenario:
 * - Manaki `isDataReady` ane oka shared flag undi. Initial ga adi `false`.
 * - Oka "Writer Thread" konchem sepu aagi, aa flag ni `true` ga set chestundi.
 * - Inko "Reader Thread" aa flag `true` ayye varaku loop lo tirugutu untundi.
 *
 * The Problem:
 * - Writer thread `isDataReady` ni `true` ga marchina, aa change tana local CPU cache lo matrame undipovachu.
 * - Main memory ki aa change raasi, reader thread yokka cache update ayye varaku, reader thread ki eppatiki `false` gane kanipisthundi.
 * - Result: Reader thread gets stuck in an infinite loop!
 *
 * NOTE: Ee program eppudu infinite loop lo undipothundi ani guarantee ledu. Adi system architecture, JVM implementation, and timing meeda depend avuthundi.
 * Kani, ee code valla potential problem ni manam ardham cheskovachu. Ee samasyani `volatile` tho ela solve cheyalo manam next lessons lo chuddam.
 */
public class VisibilityProblem {

    // Ee flag ni threads share cheskuntunnayi.
    // 'volatile' keyword ledu, so visibility guarantees levu.
    private static boolean isDataReady = false;

    public static void main(String[] args) {
        // Reader Thread
        Thread readerThread = new Thread(() -> {
            System.out.println("Reader thread: Data kosam waiting...");
            // Ee loop lo reader thread stuck aipovachu
            while (!isDataReady) {
                // Just spinning... idi CPU ni kuda waste chestundi (Busy-waiting)
            }
            System.out.println("Reader thread: Data ready ayyindi! Pani modalupedutunna.");
        }, "ReaderThread");

        // Writer Thread
        Thread writerThread = new Thread(() -> {
            System.out.println("Writer thread: Data prepare chestunnanu...");
            try {
                // Konchem time teeskuni data prepare chestunattu simulate cheddam
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Ippudu data ready ayyindi, so flag ni set cheddam
            isDataReady = true;
            System.out.println("Writer thread: Data ready flag ni `true` ga set chesa.");
        }, "WriterThread");

        readerThread.start();
        writerThread.start();
    }
}
