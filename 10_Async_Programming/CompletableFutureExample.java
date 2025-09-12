import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ee example lo manam `CompletableFuture` yokka nijamaina power ento chuddam.
 * Manam oka complex, multi-step asynchronous workflow ni build cheddam.
 *
 * Scenario:
 * Oka user dashboard ni load cheyali. Daaniki manaki ee data kavali:
 * 1. First, get the User object by user ID.
 * 2. Aa User object vachaka, daani nunchi `userId` teeskuni, aa user yokka recent orders ni fetch cheyali (oka separate API call).
 * 3. Ade samayam lo (parallel ga), aa user yokka "enrichment data" (like marketing preferences) ni inko API nunchi fetch cheyali.
 * 4. Finally, ee orders and enrichment data ni combine chesi, oka final `DashboardData` object ni create cheyali.
 * 5. Ee process lo ekkadaina error vasthe, daanini gracefully handle cheyali.
 *
 * Ee antha `future.get()` tho cheste, code chala complex and slow ga untundi.
 * `CompletableFuture` tho chala elegant ga, non-blocking way lo cheyochu.
 */
public class CompletableFutureExample {

    // Manam I/O-bound tasks chestunnam kabatti, oka virtual thread executor vadadam.
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) {
        System.out.println("Dashboard data fetch modalaindi...");

        long userId = 123L;

        // Ekkada mana asynchronous pipeline start avuthundi.
        CompletableFuture<String> dashboardFuture =
                fetchUser(userId)
                        .thenComposeAsync(user -> {
                            // User object vachaka, ee block execute avuthundi.
                            // Ikkada manam rendu kottha async tasks ni parallel ga start chestunnam.
                            CompletableFuture<List<String>> ordersFuture = fetchOrders(user.id());
                            CompletableFuture<String> enrichmentFuture = fetchEnrichment(user.id());

                            // Aa rendu results vachaka, vaatini combine cheyali.
                            return ordersFuture.thenCombine(enrichmentFuture, (orders, enrichment) -> {
                                System.out.println("Orders and Enrichment data combine chestunnam...");
                                return new DashboardData(user, orders, enrichment).toString();
                            });
                        })
                        .exceptionally(error -> {
                            // Paina unna chain lo ekkadaina exception vasthe, ee block ki vastundi.
                            System.err.println("Pipeline lo error vachindi: " + error.getMessage());
                            return "Sorry, dashboard load cheyalekapoyamu. Default data chupistunnam.";
                        });

        // Main thread ee lopu vere pani cheskovachu.
        System.out.println("Main thread: Pipeline start chesesa. Naa pani nenu cheskuntunna...");

        // Final ga result kosam wait chesi, print cheddam.
        String finalDashboard = dashboardFuture.join(); // join() anedi get() laantide, kani unchecked exception istundi.
        System.out.println("\n----------- FINAL DASHBOARD -----------");
        System.out.println(finalDashboard);
        System.out.println("------------------------------------");

        executor.shutdown();
    }

    // --- Dummy API Services ---

    private static CompletableFuture<User> fetchUser(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[API] User details fetch chestunnam...");
            sleep(1000); // Simulate network latency
            return new User(userId, "Mawa a.k.a Jules");
        }, executor);
    }

    private static CompletableFuture<List<String>> fetchOrders(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[API] User orders fetch chestunnam...");
            sleep(1500); // Simulate network latency
            // Uncomment the line below to test exception handling
            // if (true) throw new RuntimeException("Orders service is down!");
            return List.of("Laptop Order", "Book Order", "Coffee Order");
        }, executor);
    }

    private static CompletableFuture<String> fetchEnrichment(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[API] User enrichment data fetch chestunnam...");
            sleep(1000); // Simulate network latency
            return "Marketing Preference: Tech Gadgets";
        }, executor);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- Data Records ---
    record User(long id, String name) {}
    record DashboardData(User user, List<String> orders, String enrichmentData) {}
}
