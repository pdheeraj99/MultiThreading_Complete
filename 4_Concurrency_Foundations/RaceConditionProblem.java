/**
 * Ee example lo manam "Race Condition" ante ento live ga chuddam.
 *
 * Scenario:
 * - Manaki `counter` ane oka shared variable undi. Initial ga daani value 0.
 * - Manam rendu threads ni create chesi, prathi thread ee counter ni 100,000 sarlu increment cheyali ani cheptam.
 * - Logic prakaram, final result 2 * 100,000 = 200,000 undali.
 *
 * The Problem:
 * - `counter++` anedi manaki okate operation la kanipinchina, adi atomic kaadu.
 * - Danini JVM 3 steps ga chestundi:
 *   1. Read: `counter` yokka current value ni chadavadam.
 *   2. Increment: Aa value ni 1 tho penchadam.
 *   3. Write: Aa kotha value ni `counter` ki malli raayadam.
 *
 * - Ee 3 steps madhyalo, inko thread vachi disturb cheyochu. For example:
 *   1. Thread-1 `counter` (value: 10) ni read chestundi.
 *   2. Ee lopu, Thread-2 kuda vachi `counter` (value: 10) ni read chestundi.
 *   3. Thread-1 tana daggara unna 10 ni 11 ga marchi, `counter` ki rastundi. `counter` ippudu 11.
 *   4. Thread-2 ki ee vishayam teliyadu. Adi kuda tana daggara unna 10 ni 11 ga marchi, `counter` ki rastundi.
 *
 * - Iddaru increment chesina, final result 12 avvadaniki badulu, 11 eh aindi! Okari update inkokari valla overwrite aipoindi.
 *   Deenine "Race Condition" antaru.
 *
 * Result: Ee program run chesinappudu, final `counter` value eppatiki 200,000 raadu. Prathi sari oka different, takkuva value vastundi.
 */
public class RaceConditionProblem {

    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        // Rendu threads ni create cheddam
        Thread t1 = new Thread(new CounterIncrementer(), "Thread-1");
        Thread t2 = new Thread(new CounterIncrementer(), "Thread-2");

        System.out.println("Pani modalaindi... Expected result: 200000");

        t1.start();
        t2.start();

        // Rendu threads vaati pani poorthi chese varaku main thread ni aagమని cheptunnam.
        // `join()` gurinchi manam `Thread_Class_Deep_Dive` lo nerchukunnam gurtunda?
        t1.join();
        t2.join();

        // Final result ni print cheddam.
        System.out.println("Final counter value: " + counter);
        System.out.println("Chusara? Result 200000 raledu. Data corrupt aipoindi!");
    }

    // Ee task prathi thread chestundi
    static class CounterIncrementer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                counter++;
            }
        }
    }
}
