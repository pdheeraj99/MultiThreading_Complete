import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Ee example lo manam Fork/Join framework ni "divide and conquer" strategy tho ela vadalo chuddam.
 *
 * Scenario:
 * - Manaki oka pedda array (10 million numbers) undi. Daanilo unna anni numbers yokka sum kanukkovali.
 * - Okate thread tho chesthe chala time paduthundi.
 * - Manam ee pedda task ni chinna chinna sub-tasks ga divide chesi, parallel ga execute chesi,
 *   vachina results ni combine cheddam.
 */
public class ForkJoinExample {

    public static void main(String[] args) {
        // Step 1: Data ni prepare cheddam
        long[] numbers = new long[10_000_000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        // Step 2: ForkJoinPool ni create cheddam.
        // By default, adi mee system lo enni CPU cores unte anni threads ni create chestundi.
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        System.out.println("Parallelism level (threads): " + forkJoinPool.getParallelism());

        // Step 3: Main task ni create chesi, pool ki submit cheddam.
        SumTask mainTask = new SumTask(numbers, 0, numbers.length);
        long startTime = System.currentTimeMillis();
        long result = forkJoinPool.invoke(mainTask); // invoke() anedi task complete ayye varaku wait chestundi.
        long endTime = System.currentTimeMillis();

        System.out.println("Fork/Join tho vachina result: " + result);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        // Normal ga chesthe entha time paduthundo chuddam
        long expectedSum = 0;
        startTime = System.currentTimeMillis();
        for (long number : numbers) {
            expectedSum += number;
        }
        endTime = System.currentTimeMillis();
        System.out.println("\nNormal loop tho vachina result: " + expectedSum);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    /**
     * Ee class manam cheyalsina task ni represent chestundi.
     * Idi RecursiveTask ni extend chesindi, endukante manaki oka `long` result kavali.
     */
    static class SumTask extends RecursiveTask<Long> {
        // Ee threshold kanna takkuva elements unte, inka divide cheyakunda direct ga sum cheddam.
        private static final int THRESHOLD = 10_000;
        private final long[] numbers;
        private final int start;
        private final int end;

        public SumTask(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            // Base Case: Task chinnadiga unte, direct ga compute chey.
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += numbers[i];
                }
                return sum;
            }

            // Recursive Case: Task peddadiga unte, daanini divide chey.
            // 1. Fork (Divide)
            int middle = start + length / 2;
            SumTask leftTask = new SumTask(numbers, start, middle);
            SumTask rightTask = new SumTask(numbers, middle, end);

            // Oka sub-task ni ee thread lo ne asynchronus ga start chey (fork).
            leftTask.fork();

            // Inko sub-task ni ee current thread eh compute chestundi (optimization).
            long rightResult = rightTask.compute();

            // 2. Join (Combine)
            // Fork chesina leftTask yokka result kosam wait chey (join).
            long leftResult = leftTask.join();

            // Rendu results ni kalupu.
            return leftResult + rightResult;
        }
    }
}
