public class Example2_DaemonThreadExits {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("🚀 Scenario 2: Daemon Thread (The Personal Assistant) 🚀");

        // Ee worker mana 'Personal Assistant'. Veedu infinite loop lo untadu,
        // kani CEO (main thread) intiki vellipogane, veedi pani kuda aagipothundi.
        Runnable daemonTask = () -> {
            while (true) {
                try {
                    System.out.println("    👨‍💼 [Daemon Thread]: Nenu personal assistant ni. Background lo pani chestunna...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // This part is unlikely to be reached as the thread is terminated abruptly.
                    e.printStackTrace();
                }
            }
        };

        Thread personalAssistant = new Thread(daemonTask);
        personalAssistant.setDaemon(true); // <-- The most important line!

        personalAssistant.start();

        // Main thread konchem sepu pani chesi, exit aipotundi.
        System.out.println("[Main Thread]: Nenu 3 seconds pani chesi, intiki velthunna...");
        Thread.sleep(3000);

        System.out.println("[Main Thread]: Naa pani aipoindi. Nenu personal assistant kosam wait cheyanu. Bye!");
        // As soon as this main thread ends, the JVM will see that only a daemon thread is left
        // and will shut down, abruptly terminating the personalAssistant thread.
    }
}
