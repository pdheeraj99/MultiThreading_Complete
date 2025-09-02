import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    // Usecase: A classic "ready-set-go" race. We have a fixed number of runners (4)
    // who must all arrive at the starting line before the race can begin.
    // CyclicBarrier is perfect for this fixed-party synchronization.

    public static void main(String[] args) {
        final int numRunners = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numRunners);

        // The barrier action is a Runnable that gets executed once when the last
        // party arrives at the barrier. It's perfect for a "GO!" signal.
        Runnable barrierAction = () -> System.out.println("All runners are at the line. RACE START! 🔥");

        // Create a barrier for 4 parties with the specified action.
        CyclicBarrier barrier = new CyclicBarrier(numRunners, barrierAction);

        System.out.println("Getting 4 runners to the starting line...");
        for (int i = 1; i <= numRunners; i++) {
            executor.submit(new Runner(barrier, "Runner-" + i));
        }

        executor.shutdown();
    }
}

class Runner implements Runnable {
    private final CyclicBarrier barrier;
    private final String name;

    public Runner(CyclicBarrier barrier, String name) {
        this.barrier = barrier;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            // Simulate the time it takes for the runner to get to the starting line.
            System.out.println(name + ": Heading to the starting line...");
            Thread.sleep((long) (Math.random() * 3000) + 1000);
            System.out.println(name + ": At the starting line, waiting for others.");

            // All threads call await() and block here until all 4 runners have arrived.
            barrier.await();

            // This line is only reached after the barrier is tripped.
            System.out.println(name + ": And they're off!");

        } catch (InterruptedException | BrokenBarrierException e) {
            // BrokenBarrierException is thrown if another thread was interrupted
            // while waiting, or if the barrier is reset.
            System.out.println(name + " was disrupted!");
            Thread.currentThread().interrupt();
        }
    }
}
