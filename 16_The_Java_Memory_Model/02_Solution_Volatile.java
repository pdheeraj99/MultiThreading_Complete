public class Main {

    private static int x = 0;
    // By declaring 'ready' as volatile, we introduce a "happens-before" relationship.
    // A write to a volatile variable happens before any subsequent read of that same variable.
    private static volatile boolean ready = false;

    // Usecase: Demonstrating how 'volatile' solves the visibility and reordering problem.
    // The 'volatile' keyword on 'ready' ensures two things:
    // 1. Visibility: The change to 'ready' is immediately flushed to main memory.
    // 2. Ordering: The write to 'x' cannot be reordered to occur after the write to 'ready'.
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running visibility test with 'volatile'. This should NOT produce an inconsistent read.");

        for (int i = 0; i < 100_000; i++) {
            x = 0;
            ready = false;

            Thread writer = new Thread(() -> {
                // The Java Memory Model guarantees that the write to 'x' will happen
                // before the write to the volatile 'ready' variable.
                x = 42;
                ready = true;
            });

            Thread reader = new Thread(() -> {
                // Because 'ready' is volatile, when we read it as 'true', we are
                // guaranteed to see all writes that happened before the write to 'ready'.
                // This includes the write to 'x'.
                if (ready) {
                    if (x == 0) {
                        // This block should now be unreachable.
                        System.out.println("INCONSISTENT READ! This should never happen with volatile.");
                    }
                }
            });

            writer.start();
            reader.start();

            writer.join();
            reader.join();
        }

        System.out.println("Test finished. No inconsistent reads should have been reported.");
    }
}
