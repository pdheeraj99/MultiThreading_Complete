# 24. Capstone Projects - Put Your Knowledge to the Test! 🛠️

Mawa, congratulations! You have reached the end of the series. You have learned everything from the basics of threads to the cutting edge of modern Java concurrency. Theory is great, but the only way to truly achieve **mastery** is by building things.

This final chapter provides some ideas for capstone projects. Pick one that sounds interesting and build it from scratch. This will be the ultimate test of your knowledge and the best preparation for your interviews.

---

## Project Idea 1: A Concurrent Web Crawler

**The Goal:** Build a program that, given a starting URL (like `https://en.wikipedia.org/wiki/Java_(programming_language)`), downloads the page, finds all the links on it, and then concurrently downloads those pages. It should continue this process up to a certain depth.

**Why it's a great project:** This is a classic concurrency problem that involves I/O, data processing, and managing shared state.

**Key Concepts you will use:**
*   **`ExecutorService` with Virtual Threads:** Perfect for the I/O-bound task of downloading web pages. `newVirtualThreadPerTaskExecutor()` is your best friend here.
*   **`CompletableFuture`:** You can create a very elegant pipeline:
    1.  `supplyAsync` to download a page's HTML.
    2.  `thenApply` to parse the HTML and extract a list of links.
    3.  `thenAccept` to submit the new links back to the executor for crawling.
*   **`ConcurrentHashMap`:** You need a thread-safe way to keep track of URLs you have already visited to avoid getting stuck in infinite loops (e.g., page A links to B, and B links back to A).
*   **`BlockingQueue` (Alternative Design):** You could also design this using a producer-consumer model. One or more "Parser" threads produce new URLs and `put` them onto a `BlockingQueue`. Multiple "Downloader" threads `take` URLs from the queue and download them.

---

## Project Idea 2: A Parallel Log File Analyzer

**The Goal:** Build a command-line tool that can analyze a very large log file (or a directory of log files) in parallel to find certain patterns or calculate statistics (e.g., count the number of "ERROR" lines, find the IP address with the most requests).

**Why it's a great project:** This tests your ability to handle CPU-bound and I/O-bound work efficiently for data processing.

**Key Concepts you will use:**
*   **`Fork/Join Framework`:** If you are processing a single, massive file, you can use the Fork/Join framework to split the file into byte ranges and assign different threads to process different chunks of the file.
*   **`ExecutorService` with Platform Threads:** If you are processing a directory of many smaller files, you can create a `FixedThreadPool` and submit a task for each file. Since reading from the disk is I/O, you could also experiment with virtual threads here, but a platform thread pool is also a valid choice.
*   **`AtomicLong` / `LongAdder`:** When counting statistics (like the total number of errors), you will need a thread-safe way to aggregate the counts from all the parallel tasks. `AtomicLong` is good, and `LongAdder` is even better under high contention.

---

## Project Idea 3: A Live Stock Ticker Dashboard

**The Goal:** Build a simple GUI application (using Swing or JavaFX) that simulates a live stock ticker. It should be able to handle a high-frequency stream of price updates for multiple stocks and display them without freezing the UI.

**Why it's a great project:** This forces you to deal with UI thread safety and handling streams of asynchronous data.

**Key Concepts you will use:**
*   **Reactive Streams (`Flow` API or a library like RxJava):** The perfect model for this. You can have a `Publisher` that generates random stock price updates. Your UI components will be `Subscriber`s that react to these updates.
*   **Thread-Safety in UIs:** You will learn that you cannot update a UI component (like a `JLabel` or `TableView`) from any thread other than the dedicated UI thread (the Event Dispatch Thread in Swing). You'll need to use utilities like `SwingUtilities.invokeLater()` to schedule UI updates correctly.
*   **`ExecutorService`:** To run the data generation (the `Publisher`) in the background so it doesn't block the UI thread.

---

## Final Words

Mawa, ee journey lo nuvvu chala nerchukunnav. Ippudu, build cheyadam start chey. Oka project ni select chesko, daanini complete chey, and nee resume and GitHub lo pettu. This is the final step to becoming a true 30lpa Software Engineer.

All the best! It was a pleasure building this course with you. 🔥🚀
