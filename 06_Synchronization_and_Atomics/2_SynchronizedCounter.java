import java.util.ArrayList;
import java.util.List;

// Solution with `synchronized` keyword.
class SynchronizedCounter {
    private int count = 0;

    // By adding 'synchronized', we ensure only one thread can execute this method at a time.
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}

public class Example2_SynchronizedCounter {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 SynchronizedCounter Demo: The `synchronized` Solution 🚀");
        SynchronizedCounter counter = new SynchronizedCounter();

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
