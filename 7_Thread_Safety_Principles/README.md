# Stage 2.4: Thread Safety Design Principles - Code ni Safe ga Design cheyadam

Manam ippativaraku `synchronized`, `ReentrantLock`, `CountDownLatch` lanti chala powerful tools chusam. Kani, ee tools ni എവിടെ, ఎలా vadali anede asalu challenge. Just tools unte saripodu, vaatini use chese oka manchi "design" or "strategy" kavali.

Ee lesson lo, manam alanti konni important design principles gurinchi matladukuntam.

---

### Locking Strategy: Coarse-Grained vs. Fine-Grained

Manam oka shared resource ni kapadaniki lock vestam. Kani, entha varaku lock veyali? Oka chinna part ki matrame na, leda motthaniki kalipi okate lock ah? Deenine locking strategy antaru.

#### 1. Coarse-Grained Locking (Pedda Lock)

*   **Ante enti?** Anni methods or anni critical sections ki kalipi, okate okka pedda lock vadatam.
*   **Example:** Oka `BankAccount` object undi anukondi. `deposit()`, `withdraw()`, `getBalance()`, `getHistory()`... ee anni methods ki kalipi `synchronized(this)` ane okate lock veyadam.
*   **Advantages:**
    *   **Simple:** Implement cheyadaniki chala easy. Thappulu jaragadaniki chance takkuva.
    *   **Safe:** Deadlock lanti problems takkuva ga vastayi.
*   **Disadvantages:**
    *   **Poor Performance (Low Concurrency):** Okate lock undatam valla, okate sari okka thread matrame ee methods lo edokati cheyagaladu. Oka thread `getHistory()` (read operation) chestunte, inko thread `deposit()` (write operation) cheyaleka wait cheyali. Asalu sambandham leni panulaki kuda okari kosam okaru wait chestaru.

#### 2. Fine-Grained Locking (Chinna Chinna Locks)

*   **Ante enti?** Okate pedda lock ki badulu, different, independent parts ki separate, chinna chinna locks vadatam.
*   **Example:** `BankAccount` lo, `balance` update cheyadaniki oka `balanceLock` vadatam. `transactionHistory` ni update cheyadaniki inko `historyLock` vadatam.
*   **Advantages:**
    *   **High Performance (High Concurrency):** Oka thread `balanceLock` tho `deposit()` chestunte, ade samayam lo inko thread `historyLock` tho `getHistory()` cheyochu. Iddaru okarini okaru block cheskoru.
*   **Disadvantages:**
    *   **Complex:** Implement cheyadaniki chala kashtam. Ekkada, eppudu, e lock teeskovaali anedi jagrathaga chuskovali.
    *   **Deadlock Risk:** Chala locks unte, deadlocks vachhe chance ekkuva. (Ex: Thread-1 takes `balanceLock` and waits for `historyLock`. Thread-2 takes `historyLock` and waits for `balanceLock`. Iddariki iddaru forever wait chestu untaru!).

Ee diagram chuste meeku ee rendu strategies madhya aah theda telustundi:

```mermaid
block-diagram
    block:SharedResource["Shared Resource (e.g., Bank Account)"]
        block:Data["`balance`, `transactionHistory`"]
    end

    block:CoarseGrained["Coarse-Grained Locking"]
        block:Lock1["One Big Lock: `synchronized(this)`"]
            block:A["`deposit()`"]
            block:B["`withdraw()`"]
            block:C["`getHistory()`"]
        end
        label: "Simple to implement, but low concurrency."
    end

    block:FineGrained["Fine-Grained Locking"]
        block:Locks["Separate Locks"]
            block:Lock2["`balanceLock`"]
            block:Lock3["`historyLock`"]
        end
        block:Methods["Methods use specific locks"]
            block:D["`deposit()` (uses `balanceLock`)"]
            block:E["`withdraw()` (uses `balanceLock`)"]
            block:F["`getHistory()` (uses `historyLock`)"]
        end
        label: "Complex to implement, but high concurrency."
    end
```

---

### Document Your Concurrency Policy - Idi Chala Important!

*   Meeru mee class lo thread safety kosam em strategy vadaro, daanini comments lo or class-level Javadoc lo clear ga rayali.
*   **Why?** Mee class ni use chese vere developers ki teliyali, daanini ela safe ga vadalo.
*   **Example Javadoc:**
    ```java
    /**
     * This class is thread-safe.
     * It uses a fine-grained locking strategy.
     * The `balanceLock` guards the account balance for all read/write operations.
     * The `historyLock` guards the transaction history list.
     * Do not acquire both locks at the same time to avoid deadlocks.
     */
    class BankAccount {
        // ...
    }
    ```
*   Ila rayadam valla, future lo bugs rakunda kapadochu.

---

### Stage 2 Summary - What a Journey!

Ee stage lo manam Java Concurrency lo unna most important core concepts ni cover chesam.
1.  **`volatile`** tho visibility problems ni solve chesam.
2.  **`synchronized`** and **`ReentrantLock`** tho race conditions ni aapi, atomicity ni sadhincham.
3.  **`wait/notify`**, **`CountDownLatch`**, and **`CyclicBarrier`** lanti tools tho threads ni coordinate cheyadam nerchukunnam.
4.  **Executor Framework** tho thread management ni professional ga ela cheyalo telusukunnam.
5.  Ippudu, locking strategies gurinchi kuda matladukunnam.

Ippudu meeru multi-threaded applications ni safe ga and efficiently build cheyadaniki ready ga unnaru.

---

### Cliffhanger... The Next Level of Performance!

Manam ippativaraku CPU-bound, I/O-bound ani peddaga pattinchukoledu. Threads ni create chesi, tasks ni run chesam.

Kani, oka pedda, complex computation ni chala chinna chinna panuluga break chesi, vaatini parallel ga run chesi, malli kalipi final result theeskovadam ela?

Inka, లక్షల కొద్దీ I/O tasks (like network requests) unte em cheyali? Anni threads create cheyalem kada? Non-blocking, asynchronous ga panulu cheyadam ela?

Ee prashnalaki samadhanam kosam, manam **Stage 3: Advanced Concurrency & Patterns** loki adugu pettabothunnam. Get ready to explore the world of **Parallel Streams**, the revolutionary **Virtual Threads (Project Loom)**, and the powerful **`CompletableFuture`**! The game is about to change.
