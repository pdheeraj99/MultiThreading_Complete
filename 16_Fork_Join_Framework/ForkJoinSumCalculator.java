import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

// A RecursiveTask to sum an array of longs.
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    // A task is considered "small enough" if it's operating on less than this many numbers.
    public static final int THRESHOLD = 10_000;

    private final long[] numbers;
    private final int start;
    private final int end;

    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        // 1. Base Case: If the task is small enough, compute the sum directly.
        if (length <= THRESHOLD) {
            return computeSequentially();
        }

        // 2. Fork: If the task is large, split it into two sub-tasks.
        // Create the left sub-task.
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
        // Fork it to run asynchronously on another thread/core.
        leftTask.fork();

        // Create the right sub-task.
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
        // Compute the right task synchronously on the current thread. This is an optimization.
        Long rightResult = rightTask.compute();

        // 3. Join: Wait for the left sub-task to complete and get its result.
        Long leftResult = leftTask.join();

        // 4. Combine the results.
        return leftResult + rightResult;
    }

    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }


    public static void main(String[] args) {
        System.out.println("🚀 Chapter 16: Fork/Join Framework Demo 🚀");

        long[] numbers = LongStream.rangeClosed(1, 1_000_000).toArray();

        // Create a ForkJoinPool. By default, it uses a number of threads equal to your CPU cores.
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        System.out.println("[Main]: Submitting the main task to the ForkJoinPool...");
        long startTime = System.currentTimeMillis();

        // The invoke() method submits the task and waits for the final result.
        long result = forkJoinPool.invoke(new ForkJoinSumCalculator(numbers));

        long endTime = System.currentTimeMillis();

        System.out.println("[Main]: The final sum is: " + result);
        System.out.println("[Main]: Time taken: " + (endTime - startTime) + " ms");

        forkJoinPool.shutdown();
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact time will vary based on your machine's CPU cores)
================================================================================
🚀 Chapter 16: Fork/Join Framework Demo 🚀
[Main]: Submitting the main task to the ForkJoinPool...
[Main]: The final sum is: 500000500000
[Main]: Time taken: 65 ms
*/
