public class ThreadLifecycleExample {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("🚀 Chapter 3: Mana Waiter (Thread) Lifecycle 🚀");

        // Waiter cheyalsina pani (task) enti? Konchem sepu work chesi, rest theeskovadam.
        Runnable task = () -> {
            try {
                System.out.println("  [Waiter]: Order theeskvadaniki vellthunna... it will take 2 seconds.");
                Thread.sleep(2000); // Waiter customer deggara wait chestunnadu -> TIMED_WAITING
                System.out.println("  [Waiter]: Order theeskunna!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 1. NEW State: Waiter hired, but shift hasn't started.
        // Thread object create chesam, kani inka start cheyaledu.
        Thread waiterThread = new Thread(task);
        System.out.println("1. Waiter ni hire chesam. Waiter state: " + waiterThread.getState()); // Expected: NEW

        // 2. RUNNABLE State: Waiter is on the floor, ready to take orders.
        // .start() call cheyagane, thread JVM scheduler ki vellipothundi.
        waiterThread.start();
        System.out.println("2. Waiter shift start chesadu. Waiter state: " + waiterThread.getState()); // Expected: RUNNABLE

        // Manager (main thread) waiter status ni check chestu untadu.
        while (waiterThread.isAlive()) {
            System.out.println("   [Manager]: Waiter status check chestunna... State: " + waiterThread.getState());
            // Main thread konchem sepu aaguthundi, waiter ki pani cheskone chance ivvadaniki.
            Thread.sleep(500); // Main thread TIMED_WAITING loki velthundi
        }

        // 3. TERMINATED State: Waiter's shift is over.
        // Waiter thread `run()` method nunchi exit aipoindi.
        System.out.println("3. Waiter shift aipoindi. Waiter state: " + waiterThread.getState()); // Expected: TERMINATED

        // Let's demonstrate the WAITING state.
        // Inko waiter (seniorWaiter) unnadu.
        Thread seniorWaiter = new Thread(() -> {
            try {
                System.out.println("\n  [Senior Waiter]: Nenu kitchen lo pani chestunna...");
                Thread.sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
        });
        seniorWaiter.start();

        // Junior waiter senior waiter kosam wait cheyali.
        Thread juniorWaiter = new Thread(() -> {
            try {
                System.out.println("    [Junior Waiter]: Nenu senior waiter pani aipoye varaku wait cheyali...");
                seniorWaiter.join(); // Enters WAITING state until seniorWaiter is TERMINATED.
                System.out.println("    [Junior Waiter]: Ok, senior waiter pani aipoindi, ippudu nenu start chesta.");
            } catch (InterruptedException e) { e.printStackTrace(); }
        });
        juniorWaiter.start();

        // Give juniorWaiter a moment to start and enter the WAITING state
        Thread.sleep(200);
        System.out.println("\nSenior waiter pani chestunnapudu, Junior waiter state: " + juniorWaiter.getState()); // Expected: WAITING
    }
}

/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact order and number of "status check" lines might vary slightly)
================================================================================

🚀 Chapter 3: Mana Waiter (Thread) Lifecycle 🚀
1. Waiter ni hire chesam. Waiter state: NEW
2. Waiter shift start chesadu. Waiter state: RUNNABLE
   [Manager]: Waiter status check chestunna... State: RUNNABLE
  [Waiter]: Order theeskvadaniki vellthunna... it will take 2 seconds.
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
  [Waiter]: Order theeskunna!
3. Waiter shift aipoindi. Waiter state: TERMINATED

  [Senior Waiter]: Nenu kitchen lo pani chestunna...
    [Junior Waiter]: Nenu senior waiter pani aipoye varaku wait cheyali...

Senior waiter pani chestunnapudu, Junior waiter state: WAITING
    [Junior Waiter]: Ok, senior waiter pani aipoindi, ippudu nenu start chesta.

*/
