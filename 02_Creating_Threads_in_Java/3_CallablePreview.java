import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Strategy 3: A task that returns a result.
 */
class Specialist implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("  [Specialist]: Investigation started... need some time. Running in " + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(2); // Simulating a long investigation
        return "The mission was successful. All data acquired.";
    }
}

public class Example3_CallablePreview {

    public static void main(String[] args) throws Exception {

        System.out.println("🚀 Strategy 3: Callable & Future Demo (Advanced Preview) 🚀");

        // 🚨 NOTE: This is a preview of a concept from Chapter 8 (ExecutorService).
        // Don't worry if you don't understand it fully yet. The main idea here is
        // that Callable tasks can return a result (Future).

        System.out.println("Main thread: " + Thread.currentThread().getName() + " started the work.");

        ExecutorService agency = Executors.newSingleThreadExecutor();
        Specialist specialist = new Specialist();
        Future<String> report = agency.submit(specialist); // Submitting the Callable task.

        System.out.println("Main thread: Specialist ni hire chesa. Report kosam waiting...");

        // The future.get() method blocks and waits for the specialist to finish and return the report.
        String specialistReport = report.get();
        System.out.println("Main thread: Finally, the specialist's report is here!");
        System.out.println("--> REPORT: " + specialistReport);

        // We must shut down the "agency" (ExecutorService) when we're done.
        agency.shutdown();

        System.out.println("Main thread: Bye! 👋");
    }
}
