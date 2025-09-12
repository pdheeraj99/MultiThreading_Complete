# Stage 3.2: Virtual Threads (Project Loom) - Concurrency ki kottha Yugam!

Manam `ExecutorService` tho konni vanda Leda veyyi threads ni manage cheyochu ani chusam. Kani, I/O-bound applications (web servers, microservices) lo okesari lakshalakoddi connections vachinappudu em cheyali? Anni platform threads (OS-level threads) create cheste, memory saripodu, system crash avuthundi. Deenine "scalability bottleneck" antaru.

Ee pedda samasyani solve cheyadanike, Java 21 lo **Virtual Threads** (Project Loom) ni introduce chesaru. Idi oka game-changer!

---

### Platform Threads vs. Virtual Threads

*   **Platform Threads:** Manam ippativaraku chusina threads anni ive. Prathi `new Thread()` ki, OS oka nijamaina, heavy-weight thread ni create chestundi. Eevi limited ga untayi (konni velu matrame).
*   **Virtual Threads:** Eevi chala lightweight threads, OS ki sambandham ledu. Veetini JVM matrame manage chestundi. Manam millions of virtual threads ni kuda create cheyochu!

### Virtual Threads Magic Ela Pani Chestundi?

*   JVM daggara konni platform threads (vaatini "carrier threads" antaru) untayi.
*   Oka virtual thread start ayinappudu, JVM daanini oka carrier thread meeda "mount" chesi run chestundi.
*   **Magic Point:** Aa virtual thread oka blocking I/O operation (like `Thread.sleep()`, network call) cheyagane, JVM daanini aa carrier thread meeda nunchi "unmount" (dimpesi) pakkana peduthundi.
*   Ippudu aa carrier thread kaali ga undi! Adi velli vere ready-ga-unna virtual thread ni run cheyochu.
*   I/O operation poorthi avvagane, JVM aa original virtual thread ni malli oka carrier thread meeda mount chesi continue chestundi.

Ee diagram chuste meeku aa magic ardham avuthundi:

```mermaid
block-diagram
    block:JVM["JVM Space"]
        block:VT["Virtual Threads (Manam create chesevi - lakshalalo undochu)"]
            block:V1["VT 1 (Network call - BLOCKED)"]
            block:V2["VT 2 (DB query - BLOCKED)"]
            block:V3["VT 3 (Ready to run)"]
            block:V4["..."]
            block:V5["VT 1,000,000"]
        end
        block:PT["Platform Threads (Carrier Pool - Konni matrame untayi)"]
            block:P1["Platform Thread 1"]
            block:P2["Platform Thread 2"]
        end
    end

    edge:V1 -- "Unmounted (pakkana pettesaru)"
    edge:V2 -- "Unmounted (pakkana pettesaru)"
    edge:P1 -- "Mounts & Runs" -- V3
    note: "V1 and V2 I/O kosam wait chestunnayi, kani vaati carrier threads kaali ga unnayi. So, P1 velli V3 ni run chestondi. Efficiency at its best!"
```

---

### Structured Concurrency - Oka Kottha ఆలోచనా విధానం

*   Virtual threads tho paatu, ee kottha concept vachindi.
*   **Idea:** Oka task ni multiple sub-tasks ga break chesinappudu, aa sub-tasks anni oka single "scope" lo run avvali. Parent task anedi tana children tasks anni poorthi ayye varaku wait cheyali.
*   **Advantages:**
    *   **Error Handling:** Oka sub-task fail aithe, migatha anni sub-tasks ni automatically cancel cheyochu.
    *   **Cancellation:** Parent task ni cancel cheste, anni children tasks kuda cancel avuthayi.
    *   **Clarity:** Code chala readable and maintainable ga untundi. "Fire and forget" threads undavu.

---

### 🚨 Trade-offs and System Design Choices 🚨

| Thread Type | Best For (Use Case) | Trade-offs (Pros & Cons) |
| :--- | :--- | :--- |
| **Platform Threads** | **CPU-bound tasks.** Long-running, heavy calculations. | **Pro:** CPU-bound work ki best performance. <br/> **Con:** Limited in number. Prathi thread ki pedda memory footprint untundi. I/O-bound tasks ki waste of resources. |
| **Virtual Threads** | **I/O-bound tasks.** High-throughput applications with many concurrent blocking operations (web servers, microservices). | **Pro:** Chala lightweight. Millions create cheyochu. Existing synchronous code (like `Thread.sleep`, JDBC calls) ni em change cheyakunda ne, asynchronous antha performance istayi. <br/> **Con:** CPU-bound tasks ki performance gain undadu. Konni `synchronized` block situations lo "pinning" ane problem vachhi, performance taggochu. |

**System Design Rule of Thumb:**
*   Mee code ekkuva sepu I/O kosam (network, DB, file) wait chestunda? -> **Virtual Threads** vadandi. Idi 99% of modern web applications ki correct choice.
*   Mee code ekkuva sepu CPU calculations (math, data analysis) chestunda? -> **Platform Threads** (managed by a `ForkJoinPool` or `ExecutorService`) vadandi.

---

### Cliffhanger... Asynchronous ga Tasks ni Kalapadam Ela?

Virtual threads anevi blocking code ni chala efficient ga chestayi. Super!

Kani, konni sarlu manaki inka control kavali.
*   Oka network call chesi, aa result vachaka, daanini use chesi inko rendu network calls parallel ga cheyali. Aa rendu results vachaka, vaatini combine chesi final result ivvali...
*   Ilanti complex, multi-step asynchronous workflows ni ela handle cheyali?
*   Oka task fail aithe, daanini gracefully handle chesi, oka default value ela ivvali?

Ee "chaining" and "composition" of asynchronous tasks kosam, Java manaki `Future` kanna chala powerful tool ichindi. Get ready to master the art of asynchronous programming with **`CompletableFuture`**. Ade mana next topic!
