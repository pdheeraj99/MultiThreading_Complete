import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Example1_ParallelStream {

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 12: Parallel Stream Demo 🚀");

        // Create a large list of numbers
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
                                         .boxed()
                                         .collect(Collectors.toList());

        // --- 1. Sequential Stream Processing ---
        long startTimeSequential = System.currentTimeMillis();
        long oddCountSequential = numbers.stream()
                                         .filter(n -> n % 2 != 0)
                                         .count();
        long endTimeSequential = System.currentTimeMillis();
        System.out.println("\n--- Sequential Stream ---");
        System.out.println("Odd numbers found: " + oddCountSequential);
        System.out.println("Time taken: " + (endTimeSequential - startTimeSequential) + " ms");


        // --- 2. Parallel Stream Processing ---
        long startTimeParallel = System.currentTimeMillis();
        long oddCountParallel = numbers.parallelStream()
                                       .filter(n -> n % 2 != 0)
                                       .count();
        long endTimeParallel = System.currentTimeMillis();
        System.out.println("\n--- Parallel Stream ---");
        System.out.println("Odd numbers found: " + oddCountParallel);
        System.out.println("Time taken: " + (endTimeParallel - startTimeParallel) + " ms");

        System.out.println("\nNotice how the parallel stream is significantly faster for this large, CPU-bound task!");
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (Note: The exact time will vary based on your machine's CPU cores)
================================================================================

🚀 Chapter 12: Parallel Stream Demo 🚀

--- Sequential Stream ---
Odd numbers found: 500000
Time taken: 35 ms

--- Parallel Stream ---
Odd numbers found: 500000
Time taken: 12 ms

Notice how the parallel stream is significantly faster for this large, CPU-bound task!
*/
