import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Usecase: We have a very large array that we want to initialize with a specific value.
// This is a task that can be easily parallelized but doesn't return a result.
// For this, RecursiveAction is the perfect tool.

public class Main {
    public static void main(String[] args) {
        // Get the common ForkJoinPool, which is a default, statically available pool.
        ForkJoinPool pool = ForkJoinPool.commonPool();
        int arraySize = 1_000_000;
        double[] array = new double[arraySize];

        System.out.println("Initializing a large array in parallel...");

        // Create the main task that covers the entire array.
        ArrayInitializerTask mainTask = new ArrayInitializerTask(array, 0, array.length);

        // Start the execution and wait for it to complete.
        pool.invoke(mainTask);

        System.out.println("Initialization complete.");
        System.out.println("First element: " + array[0]);
        System.out.println("Middle element: " + array[arraySize / 2]);
        System.out.println("Last element: " + array[array.length - 1]);
    }
}

/**
 * A RecursiveAction to initialize elements of a double array to the value 1.0.
 */
class ArrayInitializerTask extends RecursiveAction {
    // A small enough workload to be processed directly without further splitting.
    // Choosing a good threshold is key to performance.
    private static final int THRESHOLD = 10_000;

    private final double[] array;
    private final int start;
    private final int end;

    public ArrayInitializerTask(double[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        // Base case: If the segment is small enough, do the work directly.
        if (end - start <= THRESHOLD) {
            for (int i = start; i < end; i++) {
                array[i] = 1.0;
            }
        } else {
            // Recursive step: The segment is too large, split it in half.
            int mid = start + (end - start) / 2;

            // Create two subtasks for the two halves.
            ArrayInitializerTask leftTask = new ArrayInitializerTask(array, start, mid);
            ArrayInitializerTask rightTask = new ArrayInitializerTask(array, mid, end);

            // Fork both tasks and wait for them to complete.
            // invokeAll is a convenient way to do this.
            invokeAll(leftTask, rightTask);
        }
    }
}
