import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Ee example lo manam `CyclicBarrier` ni ela vadalo nerchukuntam.
 *
 * Scenario:
 * - Oka team lo mugguru (3) developers unnaru. Vallu andaru kalisi oka feature meeda pani chestunnaru.
 * - Andaru valla valla individual tasks (coding, unit testing) poorthi cheskuni,
 *   oka point (checkpoint/barrier) daggara meet avvali.
 * - Andaru aa checkpoint ki cherukunnaka matrame, andaru kalisi code ni merge chesi, integration testing start cheyali.
 *
 * Solution: `CyclicBarrier`
 * - Manam oka `CyclicBarrier` ni 3 (number of developers) ane count tho create cheddam.
 * - Prathi developer tana pani aipogane, `barrier.await()` ni pilichi, migatha vaalla kosam wait chestadu.
 * -Barrier ki oka optional `Runnable` task (barrier action) kuda ivvochu. Ee task, barrier trip ayye mundu (ante, last person vachaka),
 *   okesari execute avuthundi. Manam daanini "All developers have arrived, starting merge!" ani print cheyadaniki vadudam.
 * - Last developer `await()` pilavagane, barrier "trips" or "breaks". Appudu wait chestunna andaru okesari proceed avutharu.
 */
public class CyclicBarrierExample {

    public static void main(String[] args) {
        int numberOfDevelopers = 3;

        // Barrier trip ayyaka run avvadaniki oka task (optional)
        Runnable barrierAction = () -> System.out.println("\n*** Barrier Action: Andaru developers vachesaru! Code merge modalaindi... ***\n");

        // Barrier ni 3 parties tho and oka action tho create cheddam.
        CyclicBarrier barrier = new CyclicBarrier(numberOfDevelopers, barrierAction);

        System.out.println("Team, mee pani modalupettandi!");

        // Mugguru developers ni aahvaniddam.
        new Thread(new Developer(barrier), "Developer-Anil").start();
        new Thread(new Developer(barrier), "Developer-Sunil").start();
        new Thread(new Developer(barrier), "Developer-Kiran").start();
    }

    static class Developer implements Runnable {
        private final CyclicBarrier barrier;

        public Developer(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            String devName = Thread.currentThread().getName();
            try {
                // Step 1: Individual task
                System.out.printf("[%s] Tana code rastunnadu...\n", devName);
                Thread.sleep((long) (Math.random() * 4000 + 1000)); // Random time teskuntadu
                System.out.printf("[%s] Tana pani poorthi chesadu. Barrier daggara waiting...\n", devName);

                // Step 2: Andaru ee point daggara wait chestaru
                barrier.await();

                // Step 3: Barrier trip ayyaka, andaru ee line nunchi okesari continue avutharu.
                System.out.printf("[%s] Barrier daatesadu! Integration testing chestunnadu...\n", devName);

            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.printf("[%s] Ki edo samasya vachindi!\n", devName);
                Thread.currentThread().interrupt();
            }
        }
    }
}
