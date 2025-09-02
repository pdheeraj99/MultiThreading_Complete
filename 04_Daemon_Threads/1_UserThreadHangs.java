public class Example1_UserThreadHangs {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("🚀 Scenario 1: User Thread (The Security Guard) 🚀");
        System.out.println("[Main]: I will finish my work in 2 seconds.");

        // This worker is a 'Security Guard'. Even after the main work is done,
        // the company (JVM) will not shut down until this worker's shift is over.
        Runnable userTask = () -> {
            try {
                // This thread will run for 5 seconds.
                for (int i = 0; i < 5; i++) {
                    System.out.println("  👮 [User Thread]: On duty...");
                    Thread.sleep(1000);
                }
                System.out.println("  👮 [User Thread]: Shift over!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread securityGuard = new Thread(userTask);
        // By default, this is a User Thread. securityGuard.setDaemon(false);

        securityGuard.start();

        Thread.sleep(2000);

        System.out.println("[Main]: My work is done, but the app will not exit yet...");
        System.out.println("[Main]: The JVM will wait for the User Thread to complete.");
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Scenario 1: User Thread (The Security Guard) 🚀
[Main]: I will finish my work in 2 seconds.
  👮 [User Thread]: On duty...
  👮 [User Thread]: On duty...
[Main]: My work is done, but the app will not exit yet...
[Main]: The JVM will wait for the User Thread to complete.
  👮 [User Thread]: On duty...
  👮 [User Thread]: On duty...
  👮 [User Thread]: On duty...
  👮 [User Thread]: Shift over!
(And then the program exits)
*/
