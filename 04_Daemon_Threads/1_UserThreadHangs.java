public class Example1_UserThreadHangs {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("🚀 Scenario 1: User Thread (The Security Guard) 🚀");

        // Ee worker mana 'Security Guard'. Main pani aipoina, veedi shift aipoye varaku company (JVM) close avvadu.
        Runnable userTask = () -> {
            try {
                // This thread will run for 5 seconds.
                for (int i = 0; i < 5; i++) {
                    System.out.println("  👮 [User Thread]: Nenu security guard ni. Pani chestunna...");
                    Thread.sleep(1000);
                }
                System.out.println("  👮 [User Thread]: Naa shift aipoindi!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread securityGuard = new Thread(userTask);
        // By default, this is a User Thread. securityGuard.setDaemon(false);

        securityGuard.start();

        // If we remove the join() call, the main thread would finish, but the JVM would
        // hang for 5 seconds waiting for the securityGuard thread to finish its work.
        System.out.println("[Main Thread]: Naa pani aipoindi, but I have to wait for the Security Guard.");
        securityGuard.join();

        System.out.println("[Main Thread]: Security guard shift aipoindi. Ippudu JVM close avvochu.");
    }
}
