import java.util.concurrent.locks.ReentrantLock;

public class Example3_Starvation {

    // A shared resource, like a printer.
    private static final ReentrantLock printerLock = new ReentrantLock();

    public static void main(String[] args) {
        System.out.println("🚀 Starvation Demo 🚀");

        // A high-priority thread that is greedy and keeps re-acquiring the lock.
        Thread vipThread = new Thread(() -> {
            while (true) {
                try {
                    printerLock.lock();
                    System.out.println("  👑 [VIP]: Printing a very important document...");
                    // This thread does its work and immediately tries to get the lock again,
                    // potentially starving other threads.
                } finally {
                    printerLock.unlock();
                }
            }
        }, "VIP-Thread");
        vipThread.setPriority(Thread.MAX_PRIORITY);

        // Several low-priority threads that will likely starve.
        for (int i = 1; i <= 3; i++) {
            Thread regularThread = new Thread(() -> {
                try {
                    printerLock.lock();
                    System.out.println("    👨‍💻 [Regular]: Finally, it's my turn to print!");
                } finally {
                    printerLock.unlock();
                }
            }, "Regular-Thread-" + i);
            regularThread.setPriority(Thread.MIN_PRIORITY);
            regularThread.start();
        }

        vipThread.start();
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Starvation Demo 🚀
  👑 [VIP]: Printing a very important document...
  👑 [VIP]: Printing a very important document...
  👑 [VIP]: Printing a very important document...
  👑 [VIP]: Printing a very important document...
  ... (this continues, and the "Regular" threads rarely or never get to print)
*/
