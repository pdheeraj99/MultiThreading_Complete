# Stage 2.3: Executor Framework - Thread Management ki Oka Boss!

Manam ippativaraku `new Thread().start()` ani threads ni create chestu vacham. Kani, oka real application lo vanda Leda veyyi sarlu ila cheste emavuthundi?

**Problems with Manual Thread Creation:**
1.  **Resource Intensive:** Prathi sari `new Thread()` anagane, OS level lo oka kotha thread create avuthundi. Idi chala costly operation.
2.  **No Reuse:** Oka thread tana pani aipogane, adi `TERMINATED` state ki velli chanipotundi. Malli kotha task kosam kotha thread create cheyali. Wastage of resources!
3.  **No Control:** Enni threads create avvutunnayo, enni active ga unnayo manaki control undadu. Chala ekkuva threads create cheste, system memory out ayyi crash avvachu.

Ee problems anni solve cheyadaniki, Java developers ki oka andamaina gift icharu. Ade `java.util.concurrent.Executor` framework.

---

### ExecutorService - The Thread Manager

*   **Concept:** Ee framework lo, manam threads ni direct ga create cheyam. Manam kevalam tasks ni matrame create chesi (as `Runnable` or `Callable`), vaatini `ExecutorService` ki submit chestam.
*   `ExecutorService` daggara oka "Thread Pool" (threads yokka group) and oka "Task Queue" (panula list) untayi.
*   Adi aa queue lo nunchi tasks ni teeskuni, tana daggara unna free worker threads ki assign chestundi. Pani aipoyaka, aa thread chanipodu, malli kotha task kosam ready ga untundi. **Thread reuse!**
*   Ee process antha ee diagram lo chudochu:

```mermaid
block-diagram
    block:Client["Your Application Code"]
        block:Task1["Task 1 (Runnable)"]
        block:Task2["Task 2 (Callable<Result>)"]
    end

    block:Executor["`ExecutorService` (The Manager)"]
        block:Queue["`BlockingQueue` (Tasks waiting in line)"]
        block:Threads["Thread Pool (The Workers)"]
            block:T1["Worker Thread 1"]
            block:T2["Worker Thread 2"]
        end
    end

    edge:Client -- "`executor.submit(task)`" --> Executor
    Task1 --> Queue
    Task2 --> Queue

    edge:T1 -- "Picks up Task 1" -- Queue
    edge:T2 -- "Picks up Task 2" -- Queue
```

---

### Different Types of Thread Pools

`Executors` ane helper class use chesi manam common thread pools ni easy ga create cheyochu.

*   `Executors.newFixedThreadPool(int nThreads)`
    *   Eppudu `nThreads` anevi fixed number of threads tho untundi. Okavela anni threads busy ga unte, kotha tasks queue lo wait chestayi.
    *   **Best for:** CPU-intensive tasks, endukante manam number of threads ni CPU cores ki equal ga set cheskovachu.
*   `Executors.newCachedThreadPool()`
    *   Idi chala flexible. Avsaram unte kotha threads ni create chestundi, avasaram lekapothe unna vaatini reuse chestundi. 60 seconds varaku use cheyani threads ni terminate chestundi.
    *   **Best for:** Chala ekkuva, kani chinna chinna, short-lived tasks unna chota. (Example: web server lo prathi request ki).
*   `Executors.newSingleThreadExecutor()`
    *   Okate okka thread tho pool ni create chestundi. Anni tasks okati tarvata okati, ade thread lo run avuthayi.
    *   **Best for:** Tasks anevi sequential ga jaragali anukunnappudu.

---

### Shutting Down the ExecutorService

`ExecutorService` anedi manam aape varaku aagadu. Program exit avvadu. So, mana pani aipoyaka daanini shutdown cheyadam chala important.

*   `shutdown()`: Kotha tasks ni accept cheyadam aapesthundi. Already submit chesina tasks anni poorthi ayyaka, service shutdown avuthundi.
*   `shutdownNow()`: Ventane anni running tasks ni `interrupt()` chesi, queue lo unna tasks ni ignore chesi, service ni shutdown cheyadaniki try chestundi.

---

### Cliffhanger... Task nunchi Result Ela Vastundi?

Super! Ippudu manam threads ni chala efficiently manage cheyagalam.

Kani oka prashna undi. Manam `Runnable` ni submit cheste, adi emi return cheyadu. Mari `Callable` ni submit cheste, adi oka value ni return chestundi kada? Aa result ni manam ela theeskuntam? `executor.submit()` anedi manaki ventane oka special object ni istundi. Aa object ento telusa?

Ade **`Future`**. Future lo raaboye result ki idi oka promise anamata. Ee `Future` and `Callable` gurinchi inka deep ga telusukodaniki, get ready for our next deep dive!
