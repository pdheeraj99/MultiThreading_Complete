import java.util.ArrayList;
import java.util.List;

public class Example2_AtomicityProblem {

    static class AtomicityCounter {
        // Volatile guarantees visibility of the latest 'count' value,
        // but it does NOT make the increment operation (read-increment-write) atomic.
        private volatile int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Demonstration 2: The Atomicity Problem that `volatile` DOES NOT solve 🚀");

        AtomicityCounter counter = new AtomicityCounter();
        List<Thread> threads = new ArrayList<>();

        // Create 10 threads, each trying to increment the counter 10,000 times.
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    counter.increment();
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n[Main]: All threads finished.");
        System.out.println("Expected count: 100000");
        System.out.println("Actual count  : " + counter.getCount() + "  <-- 🚨 Uh oh! The count is wrong!");
        System.out.println("\nThis happens because count++ is not an atomic operation, and volatile doesn't fix that.");
        System.out.println("This is a race condition!");
    }
}
