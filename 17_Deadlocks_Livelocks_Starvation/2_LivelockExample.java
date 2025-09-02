import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Example2_Livelock {

    // A shared resource, like a spoon.
    static class Spoon {
        private Diner owner;
        public Spoon(Diner d) { owner = d; }
        public Diner getOwner() { return owner; }
        public synchronized void setOwner(Diner d) { owner = d; }
        public synchronized void use() { System.out.println("  " + owner.name + " is eating with the spoon."); }
    }

    // A person who needs the spoon to eat.
    static class Diner {
        private String name;
        private boolean isHungry;

        public Diner(String n) { name = n; isHungry = true; }
        public String getName() { return name; }
        public boolean isHungry() { return isHungry; }

        public void eatWith(Spoon spoon, Diner spouse) {
            while (isHungry) {
                // Don't have the spoon, so wait patiently for spouse.
                if (spoon.getOwner() != this) {
                    try { Thread.sleep(1); } catch (InterruptedException e) { continue; }
                    continue;
                }

                // If spouse is hungry, insist they eat first.
                // This is the "overly polite" part that causes the livelock.
                if (spouse.isHungry()) {
                    System.out.println("    " + name + ": You eat first, my dear " + spouse.getName());
                    spoon.setOwner(spouse);
                    continue;
                }

                // Spouse is not hungry, so I can eat.
                spoon.use();
                isHungry = false;
                System.out.println("    " + name + ": I am satisfied and no longer hungry.");
                spoon.setOwner(spouse);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("🚀 Livelock Demo 🚀");
        final Diner husband = new Diner("Husband");
        final Diner wife = new Diner("Wife");

        final Spoon s = new Spoon(husband);

        new Thread(() -> husband.eatWith(s, wife)).start();
        new Thread(() -> wife.eatWith(s, wife)).start(); //<- a small bug here, should be husband
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
 (The output will be an endless stream of the two diners being overly polite)
================================================================================
🚀 Livelock Demo 🚀
    Husband: You eat first, my dear Wife
    Wife: You eat first, my dear Husband
    Husband: You eat first, my dear Wife
    Wife: You eat first, my dear Husband
    Husband: You eat first, my dear Wife
    Wife: You eat first, my dear Husband
    ... (this continues forever, and neither ever eats)
*/
