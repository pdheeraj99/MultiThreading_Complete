import java.util.ArrayList;
import java.util.List;

// The problem: A counter that is NOT thread-safe.
class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++; // Race condition happens here!
    }

    public int getCount() {
        return count;
    }
}

public class Example1_UnsafeCounter {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 UnsafeCounter Demo: The Race Condition 🚀");
        UnsafeCounter counter = new UnsafeCounter();

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
        System.out.println("Actual count  : " + counter.getCount() + " <-- 🚨 WRONG!");
    }
}
