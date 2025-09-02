import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExample {

    public static void main(String[] args) {
        System.out.println("🚀 Chapter 11: CompletableFuture Demo 🚀");

        // A dedicated pool for our async tasks
        ExecutorService executor = Executors.newFixedThreadPool(4);

        System.out.println("[Main]: Ordering a pizza... this will be an async workflow.");

        // Start the async workflow
        CompletableFuture<String> pizzaWorkflow =
                // 1. Get the user asynchronously
                getUserDetails()
                // 2. When the user is ready, get their favorite pizza in parallel with getting offers
                .thenCompose(user -> {
                    System.out.println("  -> Got user: " + user.name + ". Now getting their favorite pizza AND special offers in parallel.");
                    CompletableFuture<String> favoritePizza = getFavoritePizza(user);
                    CompletableFuture<String> offers = getSpecialOffers();

                    // 3. When BOTH are ready, combine the results
                    return favoritePizza.thenCombine(offers, (pizza, offer) -> {
                        System.out.println("    -> Got favorite pizza: " + pizza + " and offer: " + offer);
                        return "A " + pizza + " pizza with a " + offer + " offer.";
                    });
                })
                // 4. When the order string is ready, prepare the final order
                .thenApply(finalOrder -> {
                    System.out.println("      -> Preparing final order details for: " + finalOrder);
                    return "Your Final Order: " + finalOrder;
                })
                // If anything goes wrong in the pipeline, handle the exception
                .exceptionally(ex -> {
                    System.err.println("🚨 Oops! Something went wrong: " + ex.getMessage());
                    return "Could not place order due to an error.";
                });

        // 5. Attach a final action to consume the result without blocking the main thread
        pizzaWorkflow.thenAccept(result -> {
            System.out.println("\n[Final Result]: " + result);
        });

        System.out.println("[Main]: I have set up the pizza order workflow. I am now free to do other things.");
        System.out.println("[Main]: While the pizza is being prepared, I'll do some other work...");

        // Keep the main thread alive long enough for the async workflow to complete
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    // --- Mock Async API Calls ---

    private static CompletableFuture<User> getUserDetails() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("  [API]: Getting user details...");
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
            return new User("Mawa");
        });
    }

    private static CompletableFuture<String> getFavoritePizza(User user) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("    [API]: Getting favorite pizza for " + user.name + "...");
            try { Thread.sleep(2000); } catch (InterruptedException e) { }
            return "Farmhouse";
        });
    }

    private static CompletableFuture<String> getSpecialOffers() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("    [API]: Getting special offers...");
            try { Thread.sleep(1500); } catch (InterruptedException e) { }
            // To test the exceptionally() block, uncomment the line below
            // if (true) { throw new RuntimeException("Offer service is down!"); }
            return "50% Off";
        });
    }

    static class User {
        String name;
        User(String name) { this.name = name; }
    }
}
