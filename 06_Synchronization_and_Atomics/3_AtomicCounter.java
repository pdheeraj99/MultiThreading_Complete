import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Solution with AtomicInteger.
class AtomicCounter {
    // AtomicInteger uses special hardware instructions (CAS) to be thread-safe.
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}

public class Example3_AtomicCounter {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 AtomicCounter Demo: The `Atomic` Solution 🚀");
        AtomicCounter counter = new AtomicCounter();

        List<Thread> threads = new ArrayList<>();
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

        System.out.println("Expected count: 100000");
        System.out.println("Actual count  : " + counter.getCount() + " <-- ✅ CORRECT!");
    }
}
