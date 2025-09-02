import java.util.concurrent.TimeUnit;

/**
 * Strategy 1: The class IS-A Thread.
 * Ee approach lo, mana class ye oka worker (thread) laaga maaripothundi.
 * Idi simple ga unna, it's not flexible and generally avoided.
 */
class WorkerAsThread extends Thread {
    @Override
    public void run() {
        System.out.println("  [WorkerAsThread]: Nene worker ni! My name is " + getName());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  [WorkerAsThread]: Naa pani aipoindi.");
    }
}

public class Example1_ExtendingThread {
    public static void main(String[] args) {
        System.out.println("🚀 Strategy 1: Extending Thread Demo 🚀");
        System.out.println("Main thread: " + Thread.currentThread().getName() + " started the work.");

        WorkerAsThread worker1 = new WorkerAsThread();
        worker1.start(); // Telling the worker to start.

        System.out.println("Main thread: Worker ni start chesa, vaadi pani aipoyevaraku wait chestunna...");
        try {
            worker1.join(); // Waiting for worker1 to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main thread: Worker has finished. Bye! 👋");
    }
}
