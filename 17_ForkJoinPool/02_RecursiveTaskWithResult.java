import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

// Usecase: We need to compute the sum of a very large array of numbers.
// This is a classic divide-and-conquer problem that returns a value (the sum),
// making it a perfect fit for RecursiveTask.

public class Main {
    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 1_000_000).toArray();
        ForkJoinPool pool = ForkJoinPool.commonPool();

        System.out.println("Calculating sum of a large array in parallel...");

        // Create the main task that will be recursively split.
        ArraySumTask mainTask = new ArraySumTask(numbers, 0, numbers.length);

        // Invoke the task and get the final result.
        long result = pool.invoke(mainTask);

        System.out.println("Parallel sum result: " + result);

        // For verification, calculate the expected sum mathematically.
        long n = numbers.length;
        long expectedSum = n * (n + 1) / 2;
        System.out.println("Expected sum:        " + expectedSum);
        if (result == expectedSum) {
            System.out.println("Success! The result is correct.");
        } else {
            System.out.println("Failure! The result is incorrect.");
        }
    }
}

/**
 * A RecursiveTask to compute the sum of a portion of a long array.
 * It returns a Long as its result.
 */
class ArraySumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final long[] numbers;
    private final int start;
    private final int end;

    public ArraySumTask(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        // Base case: If the task is small enough, compute the sum directly.
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }

        // Recursive step: Split the task into two smaller subtasks.
        int mid = start + length / 2;
        ArraySumTask leftTask = new ArraySumTask(numbers, start, mid);
        ArraySumTask rightTask = new ArraySumTask(numbers, mid, end);

        // Fork the left task to run in parallel.
        leftTask.fork();

        // Compute the right task directly in the current thread.
        // This is a common optimization to reduce overhead.
        long rightResult = rightTask.compute();

        // Join the left task, waiting for its result.
        long leftResult = leftTask.join();

        // Combine the results.
        return leftResult + rightResult;
    }
}
