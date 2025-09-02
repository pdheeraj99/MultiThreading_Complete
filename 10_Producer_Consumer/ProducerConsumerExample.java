import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerExample {

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 10: Producer-Consumer Demo 🚀");

        // The shared serving counter with a capacity of 5 dishes.
        BlockingQueue<String> servingCounter = new ArrayBlockingQueue<>(5);

        // Start the Chef (Producer)
        Thread chef = new Thread(new Producer(servingCounter), "Chef");
        chef.start();

        // Start two Waiters (Consumers)
        Thread waiter1 = new Thread(new Consumer(servingCounter), "Waiter-1");
        Thread waiter2 = new Thread(new Consumer(servingCounter), "Waiter-2");
        waiter1.start();
        waiter2.start();
    }
}

// The Producer (Chef)
class Producer implements Runnable {
    private final BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                String dish = "Dish-" + i;
                System.out.println("  👨‍🍳 [Chef]: Cooking " + dish);
                Thread.sleep(1000); // Time to cook
                System.out.println("  👨‍🍳 [Chef]: Placing " + dish + " on the counter.");
                queue.put(dish); // Blocks if the queue is full
            }
            // After finishing all dishes, place a "poison pill" for each consumer.
            System.out.println("  👨‍🍳 [Chef]: All dishes are cooked. Closing the kitchen.");
            queue.put("POISON_PILL");
            queue.put("POISON_PILL");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// The Consumer (Waiter)
class Consumer implements Runnable {
    private final BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            while (true) {
                System.out.println("    🤵 [" + threadName + "]: Waiting for a dish...");
                String dish = queue.take(); // Blocks if the queue is empty

                // Check for the poison pill
                if (dish.equals("POISON_PILL")) {
                    System.out.println("    🤵 [" + threadName + "]: Got the poison pill. My shift is over!");
                    // If this consumer got the pill, another consumer might still be waiting.
                    // So, we put the pill back for the other consumer to see.
                    // This is one way to handle multiple consumers.
                    queue.put("POISON_PILL");
                    break; // Exit the loop
                }

                System.out.println("    🤵 [" + threadName + "]: Serving " + dish);
                Thread.sleep(2000); // Time to serve
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
