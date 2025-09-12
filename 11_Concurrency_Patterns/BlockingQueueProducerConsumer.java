import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Ee example lo manam Producer-Consumer pattern ni `BlockingQueue` tho entha easy ga implement cheyacho chuddam.
 *
 * Manam `wait/notify` tho rasina code tho polisthe, idi chala simple, clean, and less error-prone.
 *
 * How it works:
 * - `BlockingQueue` anedi thread-safe by default.
 * - Producer `queue.put(item)` ni pilustundi. Okavela queue full aithe, `put()` method
 *   thread ni automatically `WAITING` state lo peduthundi. Manam em cheyakkarledu.
 * - Consumer `queue.take()` ni pilustundi. Okavela queue khali ga unte, `take()` method
 *   thread ni automatically `WAITING` state lo peduthundi.
 * - Manam `synchronized`, `wait()`, `notifyAll()` lanti complexity antha marchipovachu!
 */
public class BlockingQueueProducerConsumer {

    public static void main(String[] args) {
        // Oka fixed-size blocking queue ni create cheddam.
        BlockingQueue<Integer> sharedQueue = new ArrayBlockingQueue<>(5);

        // Producer thread
        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= 20; i++) {
                    System.out.println("Producing: " + i);
                    sharedQueue.put(i); // Queue full aithe, ikkade aagutundi.
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        // Consumer thread
        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    Integer item = sharedQueue.take(); // Queue khali ga unte, ikkade aagutundi.
                    System.out.println("Consumed: " + item);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producerThread.start();
        consumerThread.start();
    }
}
