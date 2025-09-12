import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ee example lo manam `AtomicInteger` use chesi Race Condition ni ela solve cheyalo chuddam.
 * Idi locks (`synchronized` or `ReentrantLock`) lekunda thread safety sadhinchadaniki oka powerful way.
 *
 * The Problem with `counter++`:
 * - Manam chusinattu, `counter++` anedi 3 steps (read, increment, write) tho untundi, adi atomic kaadu.
 *
 * The Solution: `AtomicInteger`
 * - `java.util.concurrent.atomic` package lo chala atomic classes unnayi. `AtomicInteger` vaatilo okati.
 * - Ee class lo unna methods anni (like `incrementAndGet()`, `getAndIncrement()`) **atomic** ga execute avvutayi.
 * - Ante, aa operation start ayyaka, adi poorthi ayye varaku madhyalo vere thread disturb cheyaledu.
 *
 * How does it work internally? (Compare-and-Swap or CAS)
 * - Don't worry, idi konchem advanced, kani high level lo chepta choodu.
 * - `AtomicInteger` lanti classes background lo locks vadavu. Vaatiki badulu, avi **Compare-and-Swap (CAS)** ane oka special hardware-level instruction ni vadutayi.
 * - CAS anedi ila pani chestundi:
 *   1. "Nenu ee variable (`counter`) yokka value `X` anukuntunnanu. Adi nijanga `X` eh aithe, daanini `Y` ga marchu. Lekapothe, em cheyaku."
 *   2. Ee antha operation okate atomic step lo jarugutundi.
 * - So, `incrementAndGet()` anedi oka loop lo ee CAS operation ni try chestu untundi.
 *   - "Counter value 10 anukuntunna, 11 ga marchu." --> Success!
 *   - Inko thread vachi, "Counter value 10 anukuntunna, 11 ga marchu." --> Fails! (Endukante value ippudu 11 ga undi).
 *   - Appudu aa second thread malli try chestundi: "Counter value 11 anukuntunna, 12 ga marchu." --> Success!
 * - Ee process valla, eppatiki data corrupt avvadu, and manam explicit locks vadalsina pani ledu. Deenine **lock-free programming** antaru.
 *
 * Result:
 * - Final `counter` value eppudu correct ga 200,000 vastundi.
 * - `synchronized` kanna deeni performance high contention unna scenarios lo chala better ga untundi.
 */
public class AtomicIntegerExample {

    // Manam normal `int` ki badulu, `AtomicInteger` vadutunnam.
    private static AtomicInteger counter = new AtomicInteger(0);

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
        System.out.println("Final counter value: " + counter.get());
        System.out.println("Chusara? Locks lekundane result correct ga vachindi! This is the power of Atomic classes.");
    }

    // Ee task prathi thread chestundi
    static class CounterIncrementer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                // counter++ ki badulu, ee atomic method ni vadutunnam.
                counter.incrementAndGet();
            }
        }
    }
}
