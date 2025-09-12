/**
 * Ee example lo manam "False Sharing" ki kaaranam ayye memory layout ni,
 * and daanini avoid chese "padding" technique ni conceptually chuddam.
 *
 * NOTE: Ee program ni run chesthe, meeku performance difference kanapadakapovachu.
 * False sharing anedi chala low-level effect. Daanini correctly measure cheyalante,
 * JMH (Java Microbenchmark Harness) lanti special tools kavali.
 *
 * Ee code yokka main purpose, aa memory layout structure ni meeku chupinchadame.
 */
public class FalseSharingExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Ee example kevalam concept ni chupinchadaniki matrame.");

        // Case 1: False Sharing jarige chance unna class
        runTest(new Counters());

        // Case 2: Padding tho False Sharing ni avoid chese class
        runTest(new PaddedCounters());
    }

    private static void runTest(Runnable task) throws InterruptedException {
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        long startTime = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long endTime = System.nanoTime();

        System.out.printf("Time taken for %s: %,d ns\n", task.getClass().getSimpleName(), (endTime - startTime));
    }

    // --- Case 1: Potential False Sharing ---
    private static class Counters implements Runnable {
        // Ee rendu variables memory lo pakkana pakkana undochu.
        // Okate 64-byte cache line lo fit avvochu.
        public volatile long counterA = 0;
        public volatile long counterB = 0;

        @Override
        public void run() {
            // Oka thread eppudu counterA ni matrame update chestundi.
            // Inko thread eppudu counterB ni matrame update chestundi.
            // Aina sare, okari valla inkokari performance debba tintundi.
            for (int i = 0; i < 100_000_000; i++) {
                if (Thread.currentThread().getName().equals("Thread-0")) {
                    counterA++;
                } else {
                    counterB++;
                }
            }
        }
    }

    // --- Case 2: Avoiding False Sharing with Padding ---
    // Ee technique ni mechanical sympathy antaru - hardware ni ardham cheskuni code rayadam.
    private static class PaddedCounters implements Runnable {
        // counterA
        public volatile long counterA = 0;

        // Padding: 7 extra long variables (7 * 8 = 56 bytes).
        // Ee padding valla, counterA and counterB madhya gap perigi,
        // avi veru veru cache lines lo padathayi.
        // JVM ee padding ni optimize cheyakunda undataniki, vaatini kuda access cheyochu.
        public long p1, p2, p3, p4, p5, p6, p7;

        // counterB
        public volatile long counterB = 0;

        // Final padding
        public long p8, p9, p10, p11, p12, p13, p14;

        @Override
        public void run() {
            for (int i = 0; i < 100_000_000; i++) {
                if (Thread.currentThread().getName().equals("Thread-0")) {
                    counterA++;
                } else {
                    counterB++;
                }
            }
        }
    }

    // Java 8+ lo, `@Contended` annotation ee padding ni automatically chestundi.
    // Daaniki konni special JVM flags (-XX:-RestrictContended) avasaram.
    // @jdk.internal.vm.annotation.Contended
    // public volatile long counter = 0;
}
