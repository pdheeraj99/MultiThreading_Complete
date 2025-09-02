import java.util.concurrent.TimeUnit;

/**
 * Strategy 2: The class HAS-A task (Runnable) that a Thread can run.
 * Idi "pani" ni "pani chese vaadini" separate chestundi. (Separation of Concerns)
 * This is the recommended approach.
 */
class WorkerWithTask implements Runnable {
    @Override
    public void run() {
        System.out.println("  [WorkerWithTask]: Nenu worker ni kaadu, naa daggara pani undi. Adi chestunna thread: " + Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  [WorkerWithTask]: Naa pani aipoindi.");
    }
}

public class Example2_ImplementingRunnable {
    public static void main(String[] args) {
        System.out.println("🚀 Strategy 2: Implementing Runnable Demo 🚀");
        System.out.println("Main thread: " + Thread.currentThread().getName() + " started the work.");

        // Create the task
        WorkerWithTask task = new WorkerWithTask();
        // Create the worker (Thread) and give it the task
        Thread worker = new Thread(task, "MyRunnableThread");

        worker.start();

        System.out.println("Main thread: Worker ni start chesa, vaadi pani aipoyevaraku wait chestunna...");
        try {
            worker.join(); // Waiting for the worker to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main thread: Worker has finished. Bye! 👋");
    }
}
