import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    // 1. Define a ScopedValue. It is static, final, and public.
    // It's a handle, not a container for the value itself.
    public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    // For demonstration, let's also define a problematic ThreadLocal.
    public static final ThreadLocal<String> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) {
        System.out.println("--- Running with ScopedValue (Safe) ---");
        runWithScopedValue();

        System.out.println("\n\n--- Running with ThreadLocal (Unsafe) ---");
        runWithThreadLocal();
    }

    // --- ScopedValue Demonstration ---

    private static void runWithScopedValue() {
        // The value "user-123" is bound to the USER_ID scoped value.
        // The binding is only active for the duration of the run() method.
        ScopedValue.where(USER_ID, "user-123").run(() -> {
            System.out.println("Controller: Running as user: " + USER_ID.get());
            callServiceLayer();
        });

        // Outside the 'run' block, the binding is gone.
        System.out.println("Controller: Back outside the scope. Is USER_ID bound? " + USER_ID.isBound()); // false
    }

    private static void callServiceLayer() {
        // We can access the ScopedValue here without it being passed as a parameter.
        System.out.println("Service Layer: Authenticating user: " + USER_ID.get());
    }


    // --- ThreadLocal Demonstration of the DANGER ---

    private static void runWithThreadLocal() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            // Simulate Request 1 for user "user-ABC"
            executor.submit(() -> {
                System.out.println("Request 1: Setting user to 'user-ABC'");
                USER_ID_THREAD_LOCAL.set("user-ABC");
                System.out.println("Request 1: Processing as user: " + USER_ID_THREAD_LOCAL.get());
                // Developer FORGETS to call .remove()
            }).get(); // .get() to wait for completion

            System.out.println("--- Thread is returned to the pool ---");

            // Simulate Request 2 for a different user
            executor.submit(() -> {
                // This new task gets the SAME thread from the pool, which still has the old data!
                System.out.println("Request 2: Got a thread from the pool.");
                System.out.println("Request 2: Shockingly, the user is already set to: " + USER_ID_THREAD_LOCAL.get()); // Leaked data!
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
