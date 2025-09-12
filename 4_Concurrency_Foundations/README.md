# Stage 2.1: Concurrency Foundations - Asalaina Aata Ippude Modalaindi!

Welcome to Stage 2! Stage 1 lo manam threads ni ela create cheyalo, JMM valla vache visibility problems, and `ThreadLocal` lanti simple solutions chusam. Avi manaki data ni *share cheskokunda* ela manage cheyalo nerpayi.

Kaani, real world lo, manam data ni **share cheyalsi vastundi**. Bank balance, ticket count, shared cache... ilanti vishayalu enno untayi. Appude asalaina challenge start avuthundi. Don't worry, nenu unna ga!

---

### 1. `volatile` - Visibility Problem ki Simple Solution

Manam gurtunda, `VisibilityProblem.java` lo `isDataReady` flag ni oka thread `true` ga marchina, inko thread ki kanapadaledu. Ee samasyani solve cheyadaniki mana daggara unna modati, simple ayudham `volatile`.

*   **`volatile` em chestundi?** Oka variable ni `volatile` ga declare cheste, aa variable meeda jarige anni write operations ventane main memory loki flush avvutayi. Alage, anni read operations direct ga main memory nunchi jarugutayi, CPU cache nunchi kaadu.
*   **Result:** Oka thread chesina change, antha mandiki (anni threads ki) ventane kanipistundi! No more visibility problems.
*   **Syntax:** `private volatile boolean isDataReady = false;` - anthe, `volatile` ane keyword add cheyali.
*   Mana `VolatilitySolution.java` file lo ee solution ni chudandi.

> **Interview Tip:** `volatile` anedi kevalam **visibility** ni matrame guarantee chestundi, **atomicity** ni kaadu. "Atomicity" ante ento ippudu chuddam.

---

### 2. The Race Condition - "Nenu Mundu, Ante Nenu Mundu!"

Imagine, oka cinema hall lo okate okka last ticket undi. Iddaru users (threads) okesari aa ticket book cheyadaniki try chesaru.

1.  **Thread-1:** "Ticket unda?" ani check chesindi. Yes, undi (count = 1).
2.  **Thread-2:** Ee lopu, Thread-1 ticket book cheyaka mundhe, vachi "Ticket unda?" ani check chesindi. Yes, undi (count = 1).
3.  **Thread-1:** Ticket book chesi, count ni 0 chesindi.
4.  **Thread-2:** Adi kuda ticket book chesi, count ni -1 chesindi!

Iddaru ticket book chesaru, kani undedi okate. Data corrupt aipoindi! Deenine **Race Condition** antaru. Ante, multiple threads okate resource ni okesari modify cheyadaniki race chesukovadam.

Ee process (check cheyadam, modify cheyadam) anedi **atomic** ga (ante, madhyalo evaru disturb cheyakunda okate saariga) jaragali. Ala jaraganappude race conditions vastayi.

---

### 3. `synchronized` - The Magic Lock

Race conditions ni aapi, atomicity ni sadhinchadaniki manaki `synchronized` block (or method) help chestundi.

*   `synchronized` anedi oka lock la pani chestundi. Oka `synchronized` block loki oka thread enter aithe, adi aa block ki lock vesthundi.
*   Vere thread evaraina ade `synchronized` block loki ravalani try cheste, vallu bayate wait cheyali.
*   Modati thread tana pani poorthi cheskuni, aa block nunchi bayataki vachaka lock release chestundi. Appudu, waiting lo unna threads lo okariki chance vastundi.
*   Ee diagram chuste meeku locking mechanism ardham avuthundi:

```mermaid
sequenceDiagram
    participant T1 as Thread 1
    participant T2 as Thread 2
    participant Lock as "Synchronized Block (Lock)"

    T1->>+Lock: Enters block (Acquires Lock)
    Note right of T1: Critical work...<br/>(e.g., updating count)

    T2->>Lock: Tries to enter...
    Note left of T2: Blocked! Must wait for T1 to release the lock.

    T1-->>-Lock: Exits block (Releases Lock)

    T2->>+Lock: Acquires Lock and enters
    Note right of T2: Now T2 can do its work safely.
    T2-->>-Lock: Exits block (Releases Lock)
```

*   **Bonus:** `synchronized` anedi atomicity tho paatu, **visibility** ni kuda guarantee chestundi! Ante, `synchronized` block nunchi bayataki velle mundu chesina anni changes, next aa block loki vachhe thread ki pakka ga kanipistayi.

---

### Cliffhanger... Is `synchronized` enough?

Super! Ippudu manaki race conditions ni handle cheyadaniki `synchronized` ane oka powerful tool undi. Kani, deenilo konni limitations unnayi.
*   Oka thread lock kosam anantham ga wait chestunte em cheyali?
*   Manaki read operations ki and write operations ki separate locks kavali ante?
*   Performance inka better ga cheyali ante?

Ee prashnalaki samadhanam kosam, manam inka flexible and powerful locks gurinchi telusukovali. Get ready to meet the superstar: **`ReentrantLock`**. And also, locks lekundane updates chese **Atomic Classes** gurinchi kuda chudబోతున్నాం! Stay tuned!
