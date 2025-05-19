/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A fully-featured web crawler and search engine. This program crawls a website, extracts text
 * content, indexes it for searching, and implements several improvements like rate limiting, custom
 * filters, and logging.
 */
public class CustomSearchEngine {

  // Set to track visited URLs and avoid revisiting them
  private static Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());

  // Map to store the indexed content with URLs as the keys
  private static Map<String, String> index = new ConcurrentHashMap<>();

  // Maximum number of pages to crawl to prevent infinite loops or excessive load
  private static final int MAX_CRAWL_LIMIT = 100;

  // Crawl depth limit to prevent going too deep into the website structure
  private static final int MAX_DEPTH = 5;

  // Politeness delay (in milliseconds) to avoid overwhelming the server with requests
  private static final int POLITENESS_DELAY = 1000; // 1 second delay between requests

  // Executor service for multithreaded crawling
  private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

  // Atomic counter for tracking the number of URLs crawled
  private static final AtomicInteger crawledUrls = new AtomicInteger(0);

  // Logger for error and event logging
  private static final Logger logger = Logger.getLogger(CustomSearchEngine.class.getName());

  // Queue for URLs to be crawled, ensuring breadth-first processing
  private static final BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();

  // URL filters to customize the crawl behavior (e.g., only crawl certain types of URLs)
  private static final Pattern URL_FILTER =
      Pattern.compile(".*(example\\.com|anotherdomain\\.com).*", Pattern.CASE_INSENSITIVE);

  public static void main(String[] args) {
    String startUrl = "https://example.com"; // Replace with the actual start URL
    System.out.println("Crawling started at: " + startUrl);

    // Initialize logging
    setupLogging();

    // Add the start URL to the queue
    urlQueue.add(startUrl);

    // Schedule the crawler to start at a regular interval (every 30 minutes for example)
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(
        () -> {
          String url = urlQueue.poll();
          if (url != null) {
            crawl(url, 0);
          }
        },
        0,
        30,
        TimeUnit.MINUTES);

    // Example search query
    search("example query");
  }

  /** Sets up the logger with a console handler and formatting. */
  private static void setupLogging() {
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    SimpleFormatter formatter = new SimpleFormatter();
    consoleHandler.setFormatter(formatter);
    logger.addHandler(consoleHandler);
    logger.setLevel(Level.ALL);
  }

  /**
   * Crawls the web starting from the given URL up to a specified depth. It recursively fetches URLs
   * and stores their textual content in the index. The crawl will stop if the same URL is visited,
   * if more than 100 URLs are crawled, or if the depth exceeds the limit.
   *
   * @param url The URL to start crawling from
   * @param depth The current depth level of the crawl
   */
  private static void crawl(String url, int depth) {
    // Prevent revisiting URLs and limit the crawl to a maximum of 100 pages
    if (visitedUrls.contains(url)
        || crawledUrls.get() >= MAX_CRAWL_LIMIT
        || depth > MAX_DEPTH
        || !URL_FILTER.matcher(url).matches()) {
      return;
    }

    // Increment the counter atomically as a new URL is being crawled
    crawledUrls.incrementAndGet();

    executorService.submit(
        () -> {
          try {
            // Simulate politeness delay to avoid overloading the server
            Thread.sleep(POLITENESS_DELAY);

            // Fetch the document from the URL
            Document doc =
                Jsoup.connect(url)
                    .userAgent(
                        "CustomSearchEngine/1.0 (https://github.com/your-repo)") // Set user-agent
                                                                                 // for better
                                                                                 // compliance with
                                                                                 // servers
                    .timeout(5000) // Set a timeout to avoid hanging indefinitely
                    .get();

            // Check if the content is of HTML type; skip non-HTML content like PDFs, images
            if (doc.contentType().contains("text/html")) {
              // Extract the text content from the body of the document
              String text = doc.body().text();

              // Index the content by storing the URL and its text content
              index.put(url, text);
              visitedUrls.add(url); // Mark the URL as visited

              logger.info("Crawled: " + url); // Log the crawled URL

              // Extract all links on the page to continue crawling
              Elements links = doc.select("a[href]");
              for (Element link : links) {
                String nextUrl = link.absUrl("href"); // Resolve the absolute URL
                urlQueue.add(nextUrl); // Add the next URL to the queue
              }
            } else {
              logger.info("Skipping non-HTML content at: " + url);
            }
          } catch (IOException | InterruptedException e) {
            // Handle any IO exceptions or interruption during crawling
            logger.severe("Error while crawling " + url + ": " + e.getMessage());
          }
        });
  }

  /**
   * Searches the indexed content for the given query. It prints the URLs where the query is found
   * within the indexed text content.
   *
   * @param query The search query string
   */
  private static void search(String query) {
    System.out.println("\nSearch results for: \"" + query + "\"");

    boolean found = false;
    // Iterate through the indexed pages and search for the query
    for (Map.Entry<String, String> entry : index.entrySet()) {
      if (entry.getValue().toLowerCase().contains(query.toLowerCase())) {
        // If the query is found in the page's content, print the URL
        System.out.println("Found in: " + entry.getKey());
        found = true;
      }
    }

    if (!found) {
      // If no matches are found, notify the user
      System.out.println("No results found for the query: " + query);
    }
  }

  /**
   * Logs the current crawling state, including the visited URLs and the total number of crawled
   * pages.
   */
  private static void logCrawlState() {
    System.out.println("\nCrawl state:");
    System.out.println("Visited URLs: " + visitedUrls.size());
    System.out.println("Total pages indexed: " + index.size());
  }
}
