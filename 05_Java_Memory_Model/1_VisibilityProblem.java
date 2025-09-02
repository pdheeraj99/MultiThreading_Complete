public class Example1_VisibilityProblem {

    // Ee worker pani chestune untadu, manam 'stop' cheppevaraku.
    static class Worker extends Thread {

        // 🚨 PROBLEM: Without 'volatile', the change to 'running' might not be visible to the worker thread
        // because the value might be cached in a CPU register.
        // To see the problem, remove the 'volatile' keyword. The program will likely hang.
        private volatile boolean running = true;

        @Override
        public void run() {
            System.out.println("  [Worker]: Pani start chesa, 'running' flag 'false' ayyevaraku chestune unta.");
            while (running) {
                // This "busy-wait" loop keeps the CPU hot and makes caching more likely.
            }
            System.out.println("  [Worker]: 'running' flag 'false' aipoindi. Nenu aagipothunna!");
        }

        public void stopWorker() {
            System.out.println("[Main]: Worker ni aagమని cheptunna...");
            this.running = false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Demonstration 1: Solving the Visibility Problem with `volatile` 🚀");

        Worker worker = new Worker();
        worker.start();

        // Main thread waits for a moment before signaling the worker to stop.
        Thread.sleep(1000);

        worker.stopWorker();
        worker.join();

        System.out.println("[Main]: Worker thread has terminated. Program finished successfully.");
    }
}
