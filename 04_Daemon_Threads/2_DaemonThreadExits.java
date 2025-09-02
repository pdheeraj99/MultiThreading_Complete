public class Example2_DaemonThreadExits {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("🚀 Scenario 2: Daemon Thread (The Personal Assistant) 🚀");
        System.out.println("[Main]: I will finish my work in 3 seconds.");

        // This worker is a 'Personal Assistant'. They work in the background,
        // but the company (JVM) will not wait for them to finish.
        Runnable daemonTask = () -> {
            while (true) {
                try {
                    System.out.println("    👨‍💼 [Daemon Thread]: Tidying up the office...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread personalAssistant = new Thread(daemonTask);
        personalAssistant.setDaemon(true); // <-- The most important line!

        personalAssistant.start();

        // Main thread does its work for 3 seconds and then exits.
        Thread.sleep(3000);

        System.out.println("[Main]: My work is done. I'm going home. Bye!");
        // As soon as this main thread ends, the JVM will see that only a daemon thread is left
        // and will shut down immediately, abruptly terminating the personalAssistant thread.
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Scenario 2: Daemon Thread (The Personal Assistant) 🚀
[Main]: I will finish my work in 3 seconds.
    👨‍💼 [Daemon Thread]: Tidying up the office...
    👨‍💼 [Daemon Thread]: Tidying up the office...
    👨‍💼 [Daemon Thread]: Tidying up the office...
[Main]: My work is done. I'm going home. Bye!
(And then the program exits immediately. It does not print any more messages from the daemon thread)
*/
