import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class Main {

    // Usecase: Implementing the same "ready-set-go" race using a Phaser.
    // This demonstrates how a Phaser can be used to achieve the same result as a
    // CyclicBarrier, while also highlighting the API differences.

    public static void main(String[] args) {
        final int numRunners = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numRunners);

        // To simulate CyclicBarrier's "barrier action", we must override the
        // onAdvance() method of the Phaser. This method is called when the phaser
        // advances from one phase to the next.
        Phaser phaser = new Phaser(numRunners) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                // This is our barrier action.
                System.out.println("All runners are at the line. RACE START! 🔥 (Phase " + phase + " finished)");
                // Return false to keep the phaser active. Return true to terminate it.
                return registeredParties == 0;
            }
        };

        System.out.println("Getting 4 runners to the starting line using a Phaser...");
        for (int i = 1; i <= numRunners; i++) {
            executor.submit(new PhaserRunner(phaser, "Runner-" + i));
        }

        // --- Demonstrating the flexibility of Phaser (which CyclicBarrier lacks) ---
        // Imagine a 5th runner joins the race late! With Phaser, we can just register them.
        // With CyclicBarrier, this would be impossible.
        System.out.println("\nA 5th runner decides to join late!");
        phaser.register(); // Dynamically add one more party.
        executor.submit(new PhaserRunner(phaser, "Late-Runner-5"));


        executor.shutdown();
    }
}

class PhaserRunner implements Runnable {
    private final Phaser phaser;
    private final String name;

    public PhaserRunner(Phaser phaser, String name) {
        this.phaser = phaser;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + ": Heading to the starting line...");
            Thread.sleep((long) (Math.random() * 3000) + 1000);
            System.out.println(name + ": At the starting line, waiting for others.");

            // arriveAndAwaitAdvance() is the equivalent of barrier.await().
            phaser.arriveAndAwaitAdvance();

            System.out.println(name + ": And they're off!");
            // The runner could do more work here and wait for the next phase.

        } catch (InterruptedException e) {
            System.out.println(name + " was disrupted!");
            Thread.currentThread().interrupt();
        }
    }
}
