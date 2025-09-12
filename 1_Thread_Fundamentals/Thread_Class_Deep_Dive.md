# `java.lang.Thread` Class - A Deep Dive

Welcome! Manam `new Thread()` ani use chesam kada, asalu aa `Thread` class lona em undi? Daani powers enti? Ee deep dive lo manam `Thread` class ni post-mortem cheddam. Ready ah?

---

### `Thread` Class Overview

Ee diagram `Thread` class lo unna important methods ni categories ga చూపిస్తుంది.

```mermaid
mindmap
  root((Thread Class Deep Dive))
    Constructors
      ::icon(fa fa-cogs)
      `Thread()`
      `Thread(Runnable target)`
      ``Thread(Runnable target, String name)`
    Key Static Methods (Class ki sambandinchinavi)
      ::icon(fa fa-bolt)
      `currentThread()`
      `sleep(long millis)`
      `yield()`
    Key Instance Methods (Object ki sambandinchinavi)
      ::icon(fa fa-person-running)
      `start()`
      `join()`
      `isAlive()`
      `getState()`
      `setDaemon(boolean)`
      `isDaemon()`
      `setName(String)`
      `getName()`
      `interrupt()`
    **DANGEROUS** Deprecated Methods (Asalu vadakudadu!)
      ::icon(fa fa-skull-crossbones)
      `stop()`
      `suspend()`
      `resume()`
```

---

### 1. Constructors - Thread ni ela పుట్టించాలి?

*   `public Thread()`
    *   Oka kotha, empty thread object ni create chestundi. Deeniki `run()` method lo em logic undadu. Manam ee class ni extend chesi `run()` ni override cheste tappa idi waste.
*   `public Thread(Runnable target)`
    *   Idi manam ekkuva use chesedi. Oka `Runnable` object (ante, oka task) ni aadharamga cheskuni kotha thread ni create chestundi. Thread start ayyaka, ee `target` yokka `run()` method execute avuthundi.
*   `public Thread(Runnable target, String name)`
    *   Painadani laantide, kani manam ee thread ki oka peru (`name`) kuda ivvochu. Debugging chesetappudu idi chala useful.

---

### 2. Key Static Methods - Andari Kosam Okkade

Ee methods ni manam direct ga `Thread.methodName()` ani pilustam. Particular object tho pani ledu.

*   `public static Thread currentThread()`
    *   Ee method ni ekkada pilisthe, aa line ni execute chestunna current thread object ni return chestundi. Chala useful helper!
*   `public static void sleep(long millis) throws InterruptedException`
    *   Current thread ni specified milliseconds paatu nidrapuchutundi (pause chestundi). Idi `TIMED_WAITING` state ki veltundi.
*   `public static void yield()`
    *   "Nenu konchem sepu aagutanu, vere threads ki chance ivvandi" ani Thread Scheduler ki oka hint isthundi. Kani scheduler ee hint ni pattinchukovachu, lekapovachu. Guarantee ledu.

---

### 3. Key Instance Methods - Prathi Okkariki Pratyekam

Ee methods ni manam `threadObject.methodName()` ani pilustam.

*   `public void start()`
    *   Idi **THE MOST IMPORTANT** method. Ee method ni pilisthe ne, JVM oka kottha system-level thread ni create chesi, mana `run()` method ni andulo execute cheyadam start chestundi. **Direct ga `run()` ni pilavakudadu!** Ala pilisthe, adi normal method call la main thread lone run avuthundi.
*   `public final void join() throws InterruptedException`
    *   "Nenu ee thread pani ayye varaku aagutanu." ani current thread cheppinattu. For example, `t1.join()` ani `main` thread lo pilisthe, `main` thread anedi `t1` thread pani poorthi ayye varaku wait chestundi.
*   `public final boolean isAlive()`
    *   Thread brathike unda leda ani cheptundi. `start()` ayyaka and `TERMINATED` avvaka mundu `true` istundi.
*   `public State getState()`
    *   Thread yokka current state (`NEW`, `RUNNABLE`, etc.) ni return chestundi.
*   `public final void setDaemon(boolean on)`
    *   Thread ni daemon thread ga set cheyadaniki. **Important:** `start()` cheyaka munde ee method ni pilavali.
*   `public final boolean isDaemon()`
    *   Adi daemon thread o kaado cheptundi.
*   `public final void setName(String name)` / `public final String getName()`
    *   Thread ki peru pettadaniki and teeskovadaniki.
*   `public void interrupt()`
    *   `sleep()` or `wait()` lo unna thread ni lepadaniki (disturb cheyadaniki). Thread ki oka "interruption flag" set chestundi. Thread logic lo manam ee flag ni check cheskuni, pani aapeyalo ledo decide avvali.

---

### 4. DANGER! Deprecated Methods - Veetini Enduku Vadakudadu?

Okaప్పుడు `stop()`, `suspend()`, `resume()` lanti methods undevi. Ippudu vaatini `deprecated` chesaru, ante "vaadakandi, ivi dangerous" ani அர்த்தం.

*   `public final void stop()`
    *   **Enduku Dangerous?** Ee method thread ni sudden ga, balavantham ga aapesthundi. Ala chesinappudu, adi use chestunna resources (locks, files) release avvavu. Idi data corruption ki and deadlocks ki daari teestundi. Oka manishi pattukunna important file ni laageste ela untundo, ala anamata.
*   `public final void suspend()` and `public final void resume()`
    *   **Enduku Dangerous?** `suspend()` chesinappudu, thread tana daggara unna locks ni vadaladu. Vere thread ki aa lock kavali anukondi, adi eppatiki wait chestune untundi. Idi **deadlock** ki perfect recipe!

**Golden Rule:** Ee moodu methods ni **EPPATIKI** use cheyakandi. Interviews lo "Why is `Thread.stop()` deprecated?" ani adagadam chala common question. Ippudu meeku aah answer telusu!
