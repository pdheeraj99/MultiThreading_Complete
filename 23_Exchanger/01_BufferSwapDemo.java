import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

public class Main {

    // Usecase: The classic double-buffering scenario. A producer thread fills a buffer
    // with data while a consumer thread processes a buffer. They use an Exchanger to
    // swap the full buffer for the empty one efficiently.

    public static void main(String[] args) {
        // The Exchanger is the rendezvous point for the two threads.
        // It will exchange List<String> objects (our buffers).
        Exchanger<List<String>> exchanger = new Exchanger<>();

        // The producer starts with an empty buffer to fill.
        Thread producer = new Thread(new Producer(exchanger), "Producer");

        // The consumer also starts with an empty buffer, which it will trade for a full one.
        Thread consumer = new Thread(new Consumer(exchanger), "Consumer");

        System.out.println("Starting producer and consumer threads...");
        producer.start();
        consumer.start();
    }
}

class Producer implements Runnable {
    private final Exchanger<List<String>> exchanger;
    private List<String> buffer = new ArrayList<>();

    Producer(Exchanger<List<String>> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            for (int cycle = 1; cycle <= 3; cycle++) {
                System.out.println("Producer (Cycle " + cycle + "): Filling the buffer...");
                for (int i = 1; i <= 5; i++) {
                    String item = "Item-" + ((cycle - 1) * 5 + i);
                    buffer.add(item);
                    Thread.sleep(200); // Simulate time to produce an item
                }

                System.out.println("Producer: Buffer is full. Waiting to exchange...");
                // Exchange the full buffer for an empty one from the consumer.
                buffer = exchanger.exchange(buffer);
                System.out.println("Producer: Exchange complete. Received an empty buffer.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final Exchanger<List<String>> exchanger;
    private List<String> buffer = new ArrayList<>();

    Consumer(Exchanger<List<String>> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            for (int cycle = 1; cycle <= 3; cycle++) {
                System.out.println("Consumer (Cycle " + cycle + "): Ready to exchange for a full buffer...");
                // Exchange the empty buffer for a full one from the producer.
                buffer = exchanger.exchange(buffer);
                System.out.println("Consumer: Exchange complete. Received a full buffer with " + buffer.size() + " items.");

                System.out.println("Consumer: Processing items...");
                buffer.forEach(item -> {
                    System.out.println("  - Consuming " + item);
                    try {
                        Thread.sleep(300); // Simulate time to consume
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                buffer.clear(); // Clear the buffer to make it empty for the next exchange.
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
