import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    // Usecase: Highlighting the danger of using stateful lambdas in parallel streams.
    // A stateful lambda is one that modifies shared state, which leads to race conditions.

    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 20_000).boxed().collect(Collectors.toList());
        int expectedSize = 10_000; // There are 10,000 even numbers in the range 1-20,000.

        System.out.println("--- The WRONG way to use Parallel Streams (Stateful Lambda) ---");

        // We create a shared list that we will try to add to from multiple threads.
        // This is a classic race condition. ArrayList is NOT thread-safe.
        List<Integer> unsafeList = new ArrayList<>();

        // The lambda 'e -> unsafeList.add(e)' is STATEFUL because it modifies
        // the external 'unsafeList'.
        numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .forEach(n -> unsafeList.add(n)); // DANGER ZONE!

        System.out.println("Expected size of the list: " + expectedSize);
        System.out.println("Actual size of the list:   " + unsafeList.size());
        if (unsafeList.size() != expectedSize) {
            System.out.println("FAILURE: The result is incorrect due to race conditions. Lost writes occurred.");
        } else {
            // This is extremely unlikely but theoretically possible if the execution happens to be sequential.
            System.out.println("SUCCESS (by chance): The result is correct, but the code is still dangerously wrong.");
        }
        System.out.println("(Note: The run might also fail with an ArrayIndexOutOfBoundsException)");


        System.out.println("\n\n--- The RIGHT way to use Parallel Streams (Stateless Collector) ---");

        // The correct approach is to use a Collector. The stream library knows how to
        // perform a parallel collection safely. Each thread accumulates to its own
        // intermediate list, and then the lists are merged.
        List<Integer> safeList = numbers.parallelStream()
                                        .filter(n -> n % 2 == 0)
                                        .collect(Collectors.toList()); // SAFE!

        System.out.println("Expected size of the list: " + expectedSize);
        System.out.println("Actual size of the list:   " + safeList.size());
        if (safeList.size() == expectedSize) {
            System.out.println("SUCCESS: The result is correct because the collect operation is stateless and thread-safe.");
        } else {
            System.out.println("FAILURE: Something is very wrong with the stream implementation itself.");
        }
    }
}
