import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

public class Main {

    // Usecase: Demonstrating the key feature of Phaser - dynamic party registration.
    // A main task starts, realizes it needs help, and spawns more worker tasks.
    // The Phaser allows these new tasks to join the synchronization barrier mid-stream.

    public static void main(String[] args) {
        // Start with a phaser that only waits for 1 party (the main manager task).
        Phaser phaser = new Phaser(1);
        System.out.println("Starting the main manager task...");

        Thread manager = new Thread(new ManagerTask(phaser), "Manager");
        manager.start();
    }
}

class ManagerTask implements Runnable {
    private final Phaser phaser;

    ManagerTask(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public void run() {
        // --- Phase 0: The manager starts alone ---
        System.out.println(Thread.currentThread().getName() + ": Starting Phase " + phaser.getPhase() + ". Registered parties: " + phaser.getRegisteredParties());
        System.out.println(Thread.currentThread().getName() + ": Work is too much, spawning 3 helpers.");

        // Dynamically register 3 new parties (the helpers). This is the key feature.
        phaser.bulkRegister(3);
        System.out.println(Thread.currentThread().getName() + ": Registered 3 helpers. Total parties now: " + phaser.getRegisteredParties());

        // Create and start the helper threads.
        List<Thread> helpers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Thread helper = new Thread(new HelperTask(phaser), "Helper-" + i);
            helpers.add(helper);
            helper.start();
        }

        System.out.println(Thread.currentThread().getName() + ": Finished my part of Phase 0. Waiting for helpers.");
        phaser.arriveAndAwaitAdvance(); // Waits for itself AND the 3 helpers.

        // --- Phase 1: All 4 tasks are now running in sync ---
        System.out.println(Thread.currentThread().getName() + ": Starting Phase " + phaser.getPhase() + ". All tasks are in sync.");
        // ... do more work ...
        System.out.println(Thread.currentThread().getName() + ": Phase 1 work done. Deregistering.");
        phaser.arriveAndDeregister(); // Manager deregisters.

        System.out.println(Thread.currentThread().getName() + ": Manager task finished.");
    }
}

class HelperTask implements Runnable {
    private final Phaser phaser;

    HelperTask(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public void run() {
        // --- Phase 0: The helpers join here ---
        System.out.println(Thread.currentThread().getName() + ": Starting my work in Phase " + phaser.getPhase());
        // ... do some work ...
        System.out.println(Thread.currentThread().getName() + ": Finished my part of Phase 0. Waiting for others.");
        phaser.arriveAndAwaitAdvance(); // Arrive at the barrier.

        // --- Phase 1: Running in sync with the manager ---
        System.out.println(Thread.currentThread().getName() + ": Starting Phase " + phaser.getPhase());
        // ... do more work ...
        System.out.println(Thread.currentThread().getName() + ": Phase 1 work done. Deregistering.");
        phaser.arriveAndDeregister(); // Helper deregisters.

        System.out.println(Thread.currentThread().getName() + ": Helper task finished.");
    }
}
