/**
 * Ee example lo Immutability (maarpu leni tanam) valla thread safety ela vastundo chuddam.
 *
 * Manam oka `UserProfile` aney immutable class ni create chestam.
 * Ee object ni oka sari create chesaka, daani properties (username, email) ni change cheyalem.
 *
 * Anduke, enni threads ee object ni okesari access chesina, data eppatiki consistent ga untundi.
 * Manaki `synchronized` lanti extra locks avasaram ledu.
 */

// 1. Class ni 'final' ga declare cheyali, so vere class lu deenini extend cheyalevu.
public final class ImmutabilityExample {

    // 2. Fields anni 'private' and 'final' ga undali.
    // 'final' ante, constructor lo set chesaka malli change cheyalem.
    private final String username;
    private final String email;

    // 3. Constructor lo matrame fields ni initialize cheyali.
    public ImmutabilityExample(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // 4. Setter methods undakudadu. Getters matrame undali.
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static void main(String[] args) {
        // Oka immutable UserProfile object ni create chestunnam.
        ImmutabilityExample userProfile = new ImmutabilityExample("jules_the_dev", "jules@example.com");

        // Ippudu ee okate object ni rendu separate threads tho share cheddam.
        // Rendu threads kuda ee object ni read matrame cheyagalavu, change cheyalevu.
        // Anduke ikkada race conditions or data corruption lanti problems ravu.

        Thread reader1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " chusina data: " + userProfile);
        }, "ReaderThread-1");

        Thread reader2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " chusina data: " + userProfile);
        }, "ReaderThread-2");

        reader1.start();
        reader2.start();
    }
}
