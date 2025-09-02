import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Main {

    // Usecase: A simple shared counter protected by a StampedLock using its
    // pessimistic read and write modes. This demonstrates the basic stamp-based API.
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        SharedCounter counter = new SharedCounter();

        // A writer thread that increments the counter.
        Runnable writerTask = () -> {
            for (int i = 0; i < 5; i++) {
                counter.increment();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // Several reader threads that read the counter's value.
        Runnable readerTask = () -> {
            for (int i = 0; i < 10; i++) {
                counter.getValue();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        System.out.println("Starting one writer and multiple readers using pessimistic locks...");
        executor.submit(writerTask);
        executor.submit(readerTask);
        executor.submit(readerTask);
        executor.submit(readerTask);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("\nFinal value: " + counter.getValue());
    }
}

class SharedCounter {
    private final StampedLock lock = new StampedLock();
    private int value = 0;

    /**
     * Increments the counter using an exclusive write lock.
     */
    public void increment() {
        // acquire a write lock, which returns a "stamp"
        long stamp = lock.writeLock();
        try {
            System.out.println(Thread.currentThread().getName() + ": Acquired write lock, incrementing value.");
            value++;
        } finally {
            // release the lock using the stamp
            lock.unlockWrite(stamp);
            System.out.println(Thread.currentThread().getName() + ": Released write lock.");
        }
    }

    /**
     * Gets the current value using a shared read lock.
     * @return the current value
     */
    public int getValue() {
        // acquire a read lock, which also returns a stamp
        long stamp = lock.readLock();
        try {
            System.out.println(Thread.currentThread().getName() + ": Acquired read lock.");
            // Simulate some work while holding the read lock
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) { /* ignore */ }
            System.out.println(Thread.currentThread().getName() + ": Read value: " + value);
            return value;
        } finally {
            // release the read lock using the stamp
            lock.unlockRead(stamp);
            System.out.println(Thread.currentThread().getName() + ": Released read lock.");
        }
    }
}
