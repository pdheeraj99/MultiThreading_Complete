import java.util.concurrent.Phaser;

public class Main {

    // Usecase: A fixed number of workers need to perform a task that has three distinct
    // sequential steps (phases). All workers must complete Step 1 before any worker can
    // start Step 2, and so on. This is a classic multi-phase barrier problem.

    public static void main(String[] args) {
        final int numWorkers = 3;
        // Create a Phaser and register the initial number of parties (our workers).
        final Phaser phaser = new Phaser(numWorkers);

        System.out.println("Starting 3 workers for a 3-phase task...");

        for (int i = 1; i <= numWorkers; i++) {
            Thread worker = new Thread(new Worker(phaser), "Worker-" + i);
            worker.start();
        }
    }
}

class Worker implements Runnable {
    private final Phaser phaser;

    public Worker(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public void run() {
        try {
            // --- Phase 1: Initialization ---
            System.out.println(Thread.currentThread().getName() + ": Initializing... (Phase " + phaser.getPhase() + ")");
            Thread.sleep((long) (Math.random() * 1000)); // Simulate work
            System.out.println(Thread.currentThread().getName() + ": Initialization complete. Waiting for others.");
            phaser.arriveAndAwaitAdvance(); // Arrive at the barrier and wait for others.

            // --- Phase 2: Processing ---
            System.out.println(Thread.currentThread().getName() + ": Processing... (Phase " + phaser.getPhase() + ")");
            Thread.sleep((long) (Math.random() * 1000)); // Simulate work
            System.out.println(Thread.currentThread().getName() + ": Processing complete. Waiting for others.");
            phaser.arriveAndAwaitAdvance(); // Arrive at the barrier for the second time.

            // --- Phase 3: Cleanup ---
            System.out.println(Thread.currentThread().getName() + ": Cleaning up... (Phase " + phaser.getPhase() + ")");
            Thread.sleep((long) (Math.random() * 1000)); // Simulate work
            System.out.println(Thread.currentThread().getName() + ": Cleanup complete. Deregistering.");
            phaser.arriveAndDeregister(); // Arrive and deregister from the phaser.

            System.out.println(Thread.currentThread().getName() + ": Task finished.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
