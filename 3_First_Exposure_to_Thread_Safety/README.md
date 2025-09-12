# Stage 1.3: Thread Safety ki Modati Adugulu (First Steps to Thread Safety)

Manam JMM valla vache visibility problems chusam. Ippudu, aa problems ni solve cheyadaniki konni simple and powerful techniques nerchukundam. Ee techniques tho manam "Thread-Safe" code rayadam modalupedatham.

---

### Technique 1: Immutability - Maarpu anedaniki Taavu Ledu!

*   **Immutable Object** ante, oka sari create chesaka, daani state (ante, daani lo unna data) ni manam inka eppatiki change cheyalem.
*   **Idi Thread-Safe ela avutundi?** Chala simple. Oka vishayam eppatiki maaradu anukondi, appudu enni threads okesari daanini chusina (read chesina) em problem undadu. Data consistent ga untundi. Sharing is caring, when nobody can change anything!
*   **Example:** Java lo `String` class immutable. `Integer`, `Double` lanti wrapper classes kuda immutable eh.
*   Manam kuda mana custom classes ni immutable ga design cheyochu. Ela ante:
    1.  Class ni `final` ga declare cheyali.
    2.  Fields anni `private` and `final` ga undali.
    3.  Setter methods undakudadu.
    4.  Constructor lo matrame fields ni initialize cheyali.

Ee approach chala situations lo manaki synchronization godava lekunda chestundi.

---

### Technique 2: `ThreadLocal` - Prathi Thread ki oka Personal Locker!

*   Sare, data ni eppudu change cheyakunda undalem kada? Konni sarlu prathi thread ki oka separate, private copy of a variable kavali. Appude `ThreadLocal` hero la enter avuthundi.
*   `ThreadLocal` anedi oka special variable. Deenilo set chesina value, kevalam aa particular thread ki matrame kanipistundi. Vere threads ki adi kanapadadu, vaatiki vaati separate copies untayi.
*   **Real-world Example:** Web server lo prathi user request oka thread handle chestundi anukundam. User session details or transaction ID ni `ThreadLocal` lo store cheste, vere user requests tho conflict undadu.

Ee diagram chuste `ThreadLocal` magic ento ardham avuthundi:

```mermaid
block-diagram
    block:Main["Main Program"]
        block:ThreadLocal["`ThreadLocal<String> userContext`"]
    end

    block:Threads["Web Server Threads"]
        block:T1["User 1 Request Thread"]
            block:T1Copy["userContext = 'User: Alice'"]
        end
        block:T2["User 2 Request Thread"]
            block:T2Copy["userContext = 'User: Bob'"]
        end
    end

    edge:Main -- "provides" --> T1
    edge:Main -- "provides" --> T2
```
Chusara? `userContext` anedi okate `ThreadLocal` variable, kani prathi thread ki daani personal value undi. No confusion!

---

### Stage 1 Summary - Manam Em Nerchukunnam?

Ee first stage lo manam Java Concurrency ki పునాదులు vesam.
1.  **Threads ni create cheyadam** (`Runnable`, `Thread`) and vaati lifecycle ni ardham cheskunnam.
2.  **Java Memory Model (JMM)** ane concept valla threads madhya communication lo enduku problems vastayo telusukunnam (Visibility Problem).
3.  Aa problems ni avoid cheyadaniki **Immutability** and **`ThreadLocal`** lanti simple techniques chusam.

Ippudu meeku threads ante enti, vaati basic functioning ela untundo oka clear idea vachindi.

---

### Cliffhanger... The Real Battle Begins!

Manam ippativaraku nerchukunnavi కేవలం ఆరంభం మాత్రమే. Manam data ni share cheskokunda or change cheyakunda manage chesam.

Kani... nijamaina office work lo, chala threads okate data ni (Ex: bank account balance, seat availability) okesari modify cheyalsi vastundi. Appudu em avuthundi? Oka race laaga, evaru mundu update cheste vaallade final value na? Ala chesthe data corrupt avvada?

Ee "Race Conditions" ni aapi, mana shared data ni kapadadaniki manaki pedda ayudhalu kavali. Ave **`synchronized` blocks** and **`Locks`**.

Mana next stage lo, ee powerful tools gurinchi nerchukuni, we will step into the core of concurrency. **Stage 2: Core Concurrency** awaits! Get ready for the real action!
