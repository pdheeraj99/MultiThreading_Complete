import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class Example2_ReactiveStream {

    // The Subscriber implementation
    static class NewsSubscriber implements Flow.Subscriber<String> {
        private Flow.Subscription subscription;
        private final String name;

        public NewsSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("  [" + name + "]: Subscribed! Ready to receive news.");
            this.subscription = subscription;
            // Request the first item. This concept is called "backpressure".
            this.subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            System.out.println("  [" + name + "]: GOT NEWS! --> " + item);
            // After processing the item, request the next one.
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.err.println("  [" + name + "]: Oops! An error occurred: " + throwable.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("  [" + name + "]: All news received. Subscription complete.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Chapter 12: Reactive Stream (Flow API) Demo 🚀");

        // 1. Create a Publisher. It will publish news articles (Strings).
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 2. Create a Subscriber.
        NewsSubscriber subscriber1 = new NewsSubscriber("Subscriber-1");

        // 3. Subscribe the Subscriber to the Publisher.
        publisher.subscribe(subscriber1);

        // 4. The Publisher starts publishing news items.
        // The data is "pushed" to the subscriber.
        System.out.println("[Publisher]: Publishing news...");
        publisher.submit("Java 21 is here!");
        Thread.sleep(500);
        publisher.submit("Virtual Threads are a game changer.");
        Thread.sleep(500);
        publisher.submit("Project Loom is now complete.");
        Thread.sleep(500);

        // 5. The Publisher closes the stream when it's done.
        System.out.println("[Publisher]: No more news. Closing the stream.");
        publisher.close();

        // Wait a bit for the onComplete signal to be processed.
        Thread.sleep(1000);
    }
}
/*
================================================================================
 Mawa, Nenu ee code ni run chesa! Here is the ACTUAL verified output:
================================================================================
🚀 Chapter 12: Reactive Stream (Flow API) Demo 🚀
  [Subscriber-1]: Subscribed! Ready to receive news.
[Publisher]: Publishing news...
  [Subscriber-1]: GOT NEWS! --> Java 21 is here!
  [Subscriber-1]: GOT NEWS! --> Virtual Threads are a game changer.
  [Subscriber-1]: GOT NEWS! --> Project Loom is now complete.
[Publisher]: No more news. Closing the stream.
  [Subscriber-1]: All news received. Subscription complete.
*/
