/**
 * Ee example lo manam `synchronized` keyword use chesi `RaceConditionProblem` ni ela solve cheyalo chuddam.
 *
 * Problem Recap:
 * - `counter++` anedi atomic kaadu, so multiple threads okesari daanini modify cheste data corrupt avuthundi.
 *
 * The Solution: `synchronized`
 * - Manam `counter++` ane critical operation ni oka `synchronized` block lo pedadam.
 * - `synchronized` block ki manam oka "lock object" istham. Ee example lo, manam `SynchronizedSolution.class` object ni lock ga vadutunnam.
 *   (Static context lo class object ni lock ga vadatam common practice).
 *
 * - Ippudu ela pani chestundi?
 *   1. Thread-1 vachi `synchronized` block loki enter avvadaniki try chestundi. Lock free ga undi, so adi lock teeskuni lopaliki veltundi.
 *   2. Ee time lo Thread-2 vachi `synchronized` block loki enter avvadaniki try chestundi. Kani lock already Thread-1 daggara undi.
 *   3. So, Thread-2 bayate wait chestu untundi (BLOCKED state).
 *   4. Thread-1 `counter++` pani poorthi cheskuni, block nunchi bayataki vachagane, lock ni release chestundi.
 *   5. Ippudu Thread-2 aa lock ni teeskuni, lopaliki velli `counter++` chestundi.
 *
 * Result:
 * - Okate sari okka thread matrame `counter` ni modify cheyagaladu. Read-increment-write anedi ippudu atomic aipoindi.
 * - Final `counter` value eppudu correct ga 200,000 vastundi. No more data corruption!
 */
public class SynchronizedSolution {

    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        // Rendu threads ni create cheddam
        Thread t1 = new Thread(new CounterIncrementer(), "Thread-1");
        Thread t2 = new Thread(new CounterIncrementer(), "Thread-2");

        System.out.println("Pani modalaindi... Expected result: 200000");

        t1.start();
        t2.start();

        // Rendu threads vaati pani poorthi chese varaku wait cheddam.
        t1.join();
        t2.join();

        // Final result ni print cheddam.
        System.out.println("Final counter value: " + counter);
        System.out.println("Chusara? Ee sari result correct ga vachindi! Problem solved.");
    }

    // Ee task prathi thread chestundi
    static class CounterIncrementer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                // `counter++` anedi critical section. Daani chuttu manam lock pedutunnam.
                synchronized (SynchronizedSolution.class) {
                    counter++;
                }
            }
        }
    }
}
