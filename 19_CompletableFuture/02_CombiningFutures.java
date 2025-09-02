import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    // Usecase: Building a user's dashboard. To do this, we need to fetch two independent
    // pieces of information simultaneously: the user's profile and their recent posts.
    // Once both are available, we combine them into a single "Dashboard" object.

    public static void main(String[] args) {
        ExecutorService ioExecutor = Executors.newFixedThreadPool(10);
        String userId = "user-456";

        System.out.println("Main thread: Kicking off two parallel fetches for the user dashboard.");

        // 1. Start the first async task to fetch the user's profile.
        CompletableFuture<Profile> profileFuture = CompletableFuture.supplyAsync(
                () -> fetchUserProfile(userId), ioExecutor
        );

        // 2. Start the second async task to fetch the user's posts.
        CompletableFuture<List<Post>> postsFuture = CompletableFuture.supplyAsync(
                () -> fetchUserPosts(userId), ioExecutor
        );

        System.out.println("Main thread: Both fetches are running in parallel.");

        // 3. Combine the results of the two futures when they both complete.
        CompletableFuture<Dashboard> dashboardFuture = profileFuture.thenCombine(
                postsFuture,
                (profile, posts) -> {
                    System.out.println("Pipeline thread: Both profile and posts are ready. Combining them.");
                    return new Dashboard(profile, posts);
                }
        );

        // 4. When the combined dashboard is ready, display it.
        dashboardFuture.thenAccept(dashboard -> {
            System.out.println("\n--- User Dashboard Ready ---");
            System.out.println("User: " + dashboard.profile().userName());
            System.out.println("Posts: " + dashboard.posts().size() + " posts found.");
            System.out.println("--------------------------");
        }).join(); // .join() for demo purposes to wait for completion.

        ioExecutor.shutdown();
    }

    // --- Dummy Record Classes ---
    record Profile(String userId, String userName) {}
    record Post(String postId, String content) {}
    record Dashboard(Profile profile, List<Post> posts) {}

    // --- Simulated Slow Services ---
    private static Profile fetchUserProfile(String userId) {
        System.out.println("Pipeline thread: Fetching profile for " + userId);
        try {
            Thread.sleep(1000); // Simulate I/O
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return new Profile(userId, "Jane Doe");
    }

    private static List<Post> fetchUserPosts(String userId) {
        System.out.println("Pipeline thread: Fetching posts for " + userId);
        try {
            Thread.sleep(1200); // Simulate I/O
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return List.of(new Post("post-1", "Hello World!"), new Post("post-2", "CompletableFuture is awesome."));
    }
}
