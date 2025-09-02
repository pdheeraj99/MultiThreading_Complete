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
        Thread waiterThread = new Thread(task);
        System.out.println("1. Waiter ni hire chesam. Worker state: " + waiterThread.getState()); // Expected: NEW

        // 2. RUNNABLE State: Waiter is on the floor, ready to take orders.
        waiterThread.start();
        System.out.println("2. Waiter shift start chesadu. Worker state: " + waiterThread.getState()); // Expected: RUNNABLE

        // Manager (main thread) waiter status ni check chestu untadu.
        while (waiterThread.isAlive()) {
            System.out.println("   [Manager]: Waiter status check chestunna... State: " + waiterThread.getState());
            Thread.sleep(500); // Main thread TIMED_WAITING loki velthundi
        }

        // 3. TERMINATED State: Waiter's shift is over.
        System.out.println("3. Waiter shift aipoindi. Worker state: " + waiterThread.getState()); // Expected: TERMINATED
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact order and number of "status check" lines might vary slightly)
================================================================================
🚀 Chapter 3: Mana Waiter (Thread) Lifecycle 🚀
1. Waiter ni hire chesam. Worker state: NEW
2. Waiter shift start chesadu. Worker state: RUNNABLE
   [Manager]: Waiter status check chestunna... State: RUNNABLE
  [Waiter]: Order theeskvadaniki vellthunna... it will take 2 seconds.
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
   [Manager]: Waiter status check chestunna... State: TIMED_WAITING
  [Waiter]: Order theeskunna!
3. Waiter shift aipoindi. Worker state: TERMINATED
*/
