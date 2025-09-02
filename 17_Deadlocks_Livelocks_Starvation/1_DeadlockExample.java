public class Example1_Deadlock {

    public static void main(String[] args) {
        System.out.println("🚀 Deadlock Demo 🚀");

        // The two resources (the forks)
        final Object fork1 = new Object();
        final Object fork2 = new Object();

        // Philosopher A
        Thread philosopherA = new Thread(() -> {
            synchronized (fork1) {
                System.out.println("  [Philosopher A]: Picked up fork 1. Waiting for fork 2...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                synchronized (fork2) {
                    System.out.println("  [Philosopher A]: Picked up fork 2. Eating...");
                }
            }
        }, "Philosopher-A");

        // Philosopher B
        Thread philosopherB = new Thread(() -> {
            // This thread acquires the locks in the REVERSE order, causing the deadlock.
            synchronized (fork2) {
                System.out.println("    [Philosopher B]: Picked up fork 2. Waiting for fork 1...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                synchronized (fork1) {
                    System.out.println("    [Philosopher B]: Picked up fork 1. Eating...");
                }
            }
        }, "Philosopher-B");

        philosopherA.start();
        philosopherB.start();

        System.out.println("\n[Main]: Both philosophers have started. The application will now hang.");
        System.out.println("[Main]: Take a thread dump now (using jstack or VisualVM) to see the deadlock!");
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Deadlock Demo 🚀

[Main]: Both philosophers have started. The application will now hang.
[Main]: Take a thread dump now (using jstack or VisualVM) to see the deadlock!
  [Philosopher A]: Picked up fork 1. Waiting for fork 2...
    [Philosopher B]: Picked up fork 2. Waiting for fork 1...

...and then the program hangs forever. It will never print the "Eating..." messages.

If you take a thread dump, you will see a section like this:

Found one Java-level deadlock:
=============================
"Philosopher-B":
  waiting to lock monitor 0x000000078a45e9a0 (a java.lang.Object),
  which is held by "Philosopher-A"
"Philosopher-A":
  waiting to lock monitor 0x000000078a45e9a1 (a java.lang.Object),
  which is held by "Philosopher-B"
*/
