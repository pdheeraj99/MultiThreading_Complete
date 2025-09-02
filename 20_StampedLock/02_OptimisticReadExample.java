import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Main {

    // Usecase: A shared resource (a point in a 2D space) that is read very frequently
    // (e.g., to get its distance from the origin) but written to only occasionally.
    // This is the perfect scenario for an optimistic read.
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        SharedPoint point = new SharedPoint();

        // One writer thread that moves the point every so often.
        Runnable writerTask = () -> {
            for (int i = 0; i < 3; i++) {
                point.move(1, 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        };

        // Multiple reader threads that constantly read the point's distance.
        Runnable readerTask = () -> {
            for (int i = 0; i < 100; i++) {
                point.distanceFromOrigin();
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        };

        System.out.println("Starting one writer and multiple readers using optimistic reads...");
        executor.submit(writerTask);
        executor.submit(readerTask);
        executor.submit(readerTask);
        executor.submit(readerTask);
        executor.submit(readerTask);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}

class SharedPoint {
    private final StampedLock lock = new StampedLock();
    private double x, y;

    /**
     * Moves the point using an exclusive write lock.
     */
    public void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock();
        try {
            System.out.println("Writer: Acquired write lock.");
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
            System.out.println("Writer: Released write lock. Point is now at (" + x + ", " + y + ")");
        }
    }

    /**
     * Calculates the distance from the origin using an optimistic read.
     * @return the distance from the origin.
     */
    public double distanceFromOrigin() {
        // 1. Try for an optimistic read. This does not block and returns a stamp.
        long stamp = lock.tryOptimisticRead();
        // 2. Read the values into local variables.
        double currentX = x;
        double currentY = y;

        // 3. Validate the stamp. Check if a write has occurred since step 1.
        if (!lock.validate(stamp)) {
            // A write occurred, the optimistic read failed. The data might be inconsistent.
            // We must fall back to a full, pessimistic read lock.
            System.out.println("Reader: Optimistic read failed! Falling back to pessimistic read lock.");
            stamp = lock.readLock(); // This will block if a writer is active.
            try {
                // 4. Re-read the values now that we have a guaranteed consistent view.
                currentX = x;
                currentY = y;
            } finally {
                // 5. Unlock the pessimistic read lock.
                lock.unlockRead(stamp);
            }
        } else {
            // System.out.println("Reader: Optimistic read succeeded!");
        }

        // 6. Use the consistent values to perform the calculation.
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
