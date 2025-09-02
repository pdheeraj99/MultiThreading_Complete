import java.time.Duration;

public class Main {

    // Usecase: Simple demonstration of creating and running a platform thread and a virtual thread.
    public static void main(String[] args) throws InterruptedException {
        // Create a platform thread
        Thread platformThread = new Thread(() -> {
            System.out.println("Hello from a platform thread!");
        });

        // Create a virtual thread
        Thread virtualThread = Thread.startVirtualThread(() -> {
            System.out.println("Hello from a virtual thread!");
        });

        // Start the platform thread
        platformThread.start();

        // Wait for both threads to complete
        platformThread.join();
        virtualThread.join();

        System.out.println("Main thread finished.");
    }
}
