import java.util.UUID;

/**
 * Ee example lo `ThreadLocal` ela pani chestundo, and daani real-world use case ento chuddam.
 *
 * Scenario:
 * - Oka web application undi anukundam. Prathi user request oka kottha thread lo handle avuthundi.
 * - Manam prathi request ki oka unique "Transaction ID" ivvali, logging and debugging kosam.
 * - Ee Transaction ID anedi aa request process ayye antha sepu available undali, kani vere requests tho mix avvakudadu.
 *
 * Solution:
 * - `ThreadLocal` use chesi, prathi thread ki oka separate Transaction ID copy ni store cheddam.
 * - Ila cheyadam valla, thread-1 yokka ID, thread-2 ki kanapadadu. Data safety is guaranteed!
 */
public class ThreadLocalExample {

    // 1. Oka ThreadLocal variable ni create cheddam. Idi String type values ni store chestundi.
    // `withInitial` anedi optional, kani idi initial value ni set cheyadaniki chala useful.
    private static final ThreadLocal<String> transactionId = ThreadLocal.withInitial(() -> "No-Txn-ID");

    public static void main(String[] args) {
        // Rendu separate user requests anukuni, rendu threads ni start cheddam.
        Runnable userRequest1 = new RequestHandler("User-Alice");
        Runnable userRequest2 = new RequestHandler("User-Bob");

        new Thread(userRequest1, "Request-Thread-1").start();
        new Thread(userRequest2, "Request-Thread-2").start();
    }

    // Ee class oka user request ni handle chese logic ni represent chestundi.
    static class RequestHandler implements Runnable {
        private final String userName;

        RequestHandler(String userName) {
            this.userName = userName;
        }

        @Override
        public void run() {
            // Request start avvagane, ee thread ki oka unique transaction ID ni set cheddam.
            String newTransactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            transactionId.set(newTransactionId);

            String threadName = Thread.currentThread().getName();
            System.out.printf("[%s] User '%s' kosam request start ayyindi. Transaction ID: %s%n",
                    threadName, userName, transactionId.get());

            // Ippudu ee request lo konni steps (methods) unnay anukundam.
            new ServiceA().process();
            new ServiceB().process();

            // Pani aipoyaka, ThreadLocal value ni remove cheyadam chala important!
            // Endukante, application servers lo threads ni reuse chestaru (thread pools).
            // Manam remove cheyakapothe, ee transaction ID vere request ki leak avvachu.
            transactionId.remove();
            System.out.printf("[%s] Request poorti ayyindi. Transaction ID clear cheyabadindi. Current value: %s%n",
                    threadName, transactionId.get());
        }
    }

    // Oka dummy service
    static class ServiceA {
        public void process() {
            String currentTxnId = transactionId.get();
            System.out.printf("  [Service A] Lo pani chestunna... Naa current Transaction ID: %s%n", currentTxnId);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Inko dummy service
    static class ServiceB {
        public void process() {
            String currentTxnId = transactionId.get();
            System.out.printf("  [Service B] Lo pani chestunna... Naa current Transaction ID: %s%n", currentTxnId);
        }
    }
}
