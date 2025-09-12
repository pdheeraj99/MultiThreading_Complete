# `java.util.concurrent.locks.ReentrantLock` - A Deep Dive

Manam `synchronized` gurinchi nerchukunnam. Adi chala simple and powerful. Kani, konni advanced scenarios lo, adi saripodu. Manaki inka control, inka flexibility kavali. Appude `ReentrantLock` ane ee superstar enter avuthundi!

`ReentrantLock` anedi `Lock` interface yokka implementation. Idi `synchronized` kanna chala ekkuva features istundi.

---

### `synchronized` vs `ReentrantLock` - A Quick Comparison

Ee flowchart chuste meeku oka basic idea vastundi.

```mermaid
graph TD
    A[Locking Mechanisms] --> B(Intrinsic Lock: `synchronized`);
    A --> C(Explicit Lock: `ReentrantLock`);

    subgraph B
        D["✅ Simple & Easy to Use"];
        E["✅ Automatic Lock/Unlock (Scope-based)"];
        F["❌ Limited Features"];
    end

    subgraph C
        G["✅ Flexible & Powerful"];
        H["❌ Manual Lock/Unlock (Must use `try-finally`)"];
        I["✅ Advanced Features"];
    end

    I --> I1["`tryLock()` - Wait cheyakunda try cheyadam"];
    I --> I2["Interruptible Lock - Lock kosam wait chestunna thread ni `interrupt()` cheyochu"];
    I --> I3["Fairness Policy - Waiting threads ki nyayam cheyadam"];
```

---

### Key Methods - Asalu Magic ikkade undi!

*   `void lock()`
    *   `synchronized` block start ayinatte. Lock dorike varaku ee method aagutundi (block avuthundi).
*   `void unlock()`
    *   Lock ni release chestundi. Idi **chala chala important**. Manam `unlock()` cheyadam marchipothe, vere threads eppatiki aa lock kosam wait chestune untayi, leading to a deadlock!
    *   **Golden Rule:** `unlock()` ni eppudu `finally` block lo ne pettali. Endukante, `try` block lo exception vachina sare, `finally` block execute ayyi lock release avuthundi.

    ```java
    lock.lock();
    try {
        // Your critical section code goes here...
    } finally {
        lock.unlock(); // Ee line marchipothe anthe sangathulu!
    }
    ```

*   `boolean tryLock()`
    *   Idi `lock()` laantide, kani wait cheyadu. Lock available unte, ventane lock teeskuni `true` return chestundi. Lock available lekapothe, wait cheyakunda ventane `false` return chestundi. "Chance unte cheddam, lekapothe lite," ane type anamata.
*   `boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException`
    *   Painadani laantide, kani konchem sepu wait chestundi. Specified time loపు lock dorikithe `true`, lekapothe `false`.
*   `void lockInterruptibly() throws InterruptedException`
    *   `lock()` laage wait chestundi, kani ee waiting time lo vere thread `interrupt()` cheste, `InterruptedException` throw chesi waiting aapesthundi. `synchronized` lo ee facility ledu.

---

### Special Features - What makes it a Superstar?

#### 1. Reentrancy - "Nenu na lock ni malli teeskogalanu"

*   "Reentrant" ante "re-enter-able".
*   Oka thread daggara already oka lock undi anukondi. Adi ade lock ni malli teeskodaniki try cheste, daaniki aa lock ventane dorukutundi. Adi block avvadu.
*   `synchronized` kuda reentrant eh. Anduke manam oka synchronized method nunchi inko synchronized method ni call cheyagalam. `ReentrantLock` lo kuda ide facility undi.

#### 2. Fairness Policy - "First come, first served"

*   Idi `ReentrantLock` yokka killer feature. `synchronized` lo idi ledu.
*   Lock release ayyaka, waiting lo unna threads lo దేనికి next chance ivvali?
    *   **Non-fair (Default):** Evariki istamochinattu vaallaki ivvochu (performance kosam). Ee process lo, oka thread ki eppatiki chance rakapovachu (Starvation).
    *   **Fair:** Evaru mundu vachi line lo nunchunnaro (FIFO - First-In-First-Out), vaallake next chance istundi. Starvation undadu, kani performance konchem taggutundi.
*   **Ela create cheyali?**
    *   `Lock lock = new ReentrantLock();` // Default, Non-fair
    *   `Lock lock = new ReentrantLock(true);` // Fair lock

> **Interview Tip:** "What is the difference between `synchronized` and `ReentrantLock`?" idi top-5 concurrency interview questions lo okati. Ee points anni chepthe, full marks padipotayi! `ReentrantLock` anedi `synchronized` ki replacement kaadu, adi oka advanced alternative. Simple cases ki `synchronized` eh best.
