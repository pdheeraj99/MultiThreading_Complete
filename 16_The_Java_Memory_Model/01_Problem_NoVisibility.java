public class Main {

    // These variables are shared between threads.
    // Without any synchronization, their values are not guaranteed to be visible across threads,
    // and operations on them can be reordered.
    private static int x = 0;
    private static boolean ready = false;

    // Usecase: Demonstrating what can go wrong without memory synchronization.
    // One thread (writer) prepares data and sets a flag.
    // Another thread (reader) reads the data based on the flag.
    // We expect the reader to see the prepared data (x=42) if it sees the flag (ready=true).
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running visibility test for a few seconds. Look for 'INCONSISTENT READ!'...");

        for (int i = 0; i < 100_000; i++) {
            // Reset for the next run
            x = 0;
            ready = false;

            Thread writer = new Thread(() -> {
                // These two operations are independent in the code, so a compiler or CPU
                // might reorder them for performance.
                // It could execute ready = true BEFORE x = 42.
                x = 42;
                ready = true;
            });

            Thread reader = new Thread(() -> {
                // The reader might see the write to 'ready' but not the write to 'x' due to
                // either reordering on the writer's side or caching issues.
                if (ready) {
                    if (x == 0) {
                        // This is the "impossible" state we're trying to catch.
                        // It means we saw 'ready' as true, but 'x' as its old value.
                        System.out.println("INCONSISTENT READ! ready=true but x=0");
                    }
                }
            });

            writer.start();
            reader.start();

            writer.join();
            reader.join();
        }

        System.out.println("Test finished. If you saw the 'INCONSISTENT READ' message, the JMM problem was demonstrated.");
        System.out.println("If not, it doesn't mean the code is correct; it just means the race condition didn't manifest this time.");
    }
}
