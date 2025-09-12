import java.util.LinkedList;
import java.util.Queue;

/**
 * Ee example lo manam classic "Producer-Consumer" problem ni `wait()` and `notifyAll()` use chesi solve cheddam.
 *
 * Scenario:
 * - Oka "Producer" thread undi, adi numbers (items) ni produce chesi oka shared queue (buffer) lo peduthundi.
 * - Oka "Consumer" thread undi, adi aa queue nunchi numbers ni teeskuni consume chestundi.
 *
 * Rules:
 * 1. Producer anedi queue nindipothe (capacity reach aithe), produce cheyadam aapi wait cheyali.
 * 2. Consumer anedi queue khali ga unte, consume cheyadam aapi wait cheyali.
 * 3. Producer item pettagane, wait chestunna Consumer ki signal ivvali (`notifyAll`).
 * 4. Consumer item teesko gane, wait chestunna Producer ki signal ivvali (`notifyAll`).
 *
 * Ee antha coordination `synchronized` block and `wait/notifyAll` tho sadhistham.
 */
public class ProducerConsumerExample {

    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5); // Buffer capacity is 5

        // Producer thread
        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.produce(i);
                    Thread.sleep(500); // Konchem gap ivvadaniki
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        // Consumer thread
        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.consume();
                    Thread.sleep(1000); // Konchem gap ivvadaniki
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producerThread.start();
        consumerThread.start();
    }

    static class SharedBuffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity;

        public SharedBuffer(int capacity) {
            this.capacity = capacity;
        }

        // Ee method ni okate sari okka thread matrame access cheyali (Producer or Consumer)
        public synchronized void produce(int item) throws InterruptedException {
            // Rule 1: Buffer nindi unte, wait cheyali.
            while (queue.size() == capacity) {
                System.out.println("Buffer is full. Producer is waiting...");
                wait(); // Lock ni release chesi, waiting state loki veltundi.
            }

            // Buffer lo place undi, so item ni add cheddam.
            queue.add(item);
            System.out.println("Produced: " + item);

            // Rule 3: Wait chestunna consumer(s) ki signal ivvali.
            // 'notifyAll' vadatam safe, endukante multiple consumers unna andaru wake up avutharu.
            notifyAll();
        }

        // Ee method ni kuda okate sari okka thread matrame access cheyali.
        public synchronized int consume() throws InterruptedException {
            // Rule 2: Buffer khali ga unte, wait cheyali.
            while (queue.isEmpty()) {
                System.out.println("Buffer is empty. Consumer is waiting...");
                wait(); // Lock ni release chesi, waiting state loki veltundi.
            }

            // Buffer lo item undi, so daanini teeskundam.
            int item = queue.poll();
            System.out.println("Consumed: " + item);

            // Rule 4: Wait chestunna producer(s) ki signal ivvali.
            notifyAll();
            return item;
        }
    }
}
