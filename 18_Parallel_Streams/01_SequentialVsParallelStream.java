import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    // Usecase: We have a CPU-intensive task to perform on a large dataset:
    // finding all the prime numbers in a range. This is a perfect candidate
    // to showcase the performance benefits of parallel streams.

    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 200_000).boxed().collect(Collectors.toList());
        System.out.println("Processing " + numbers.size() + " numbers on a multi-core machine.\n");

        // --- Sequential Execution ---
        Instant startSequential = Instant.now();
        long sequentialPrimeCount = numbers.stream()
                                           .filter(Main::isPrime)
                                           .count();
        Instant endSequential = Instant.now();
        Duration sequentialDuration = Duration.between(startSequential, endSequential);
        System.out.println("Sequential Stream processing took: " + sequentialDuration.toMillis() + " ms");
        System.out.println("Number of primes found: " + sequentialPrimeCount);


        // --- Parallel Execution ---
        Instant startParallel = Instant.now();
        // The ONLY change is calling .parallelStream() instead of .stream()
        long parallelPrimeCount = numbers.parallelStream()
                                         .filter(Main::isPrime)
                                         .count();
        Instant endParallel = Instant.now();
        Duration parallelDuration = Duration.between(startParallel, endParallel);
        System.out.println("\nParallel Stream processing took:   " + parallelDuration.toMillis() + " ms");
        System.out.println("Number of primes found: " + parallelPrimeCount);

        System.out.println("\n---");
        if (sequentialPrimeCount == parallelPrimeCount) {
            System.out.println("Results are consistent.");
            double speedup = (double) sequentialDuration.toMillis() / parallelDuration.toMillis();
            System.out.printf("Parallel version was %.2f times faster.\n", speedup);
        } else {
            System.out.println("Error: Results are inconsistent!");
        }
    }

    /**
     * A simple (but not most efficient) method to check if a number is prime.
     * It's intentionally made to be a bit slow to simulate a CPU-intensive task.
     * @param number the number to check
     * @return true if prime, false otherwise
     */
    private static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
