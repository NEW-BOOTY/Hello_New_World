/** Copyright © 2024 Devin B. Royal. All rights reserved. */
package com.devinroyal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Utility class to handle browser-like functionality, such as displaying page content, filtering
 * elements, and managing a basic browsing history system.
 */
public class BrowserUtils {

  // Deque to store the browsing history for "back" navigation
  private static Deque<String> history = new ArrayDeque<>();

  // Set to track visited links to avoid revisiting
  private static Set<String> visitedLinks = new HashSet<>();

  /**
   * Displays the page content in the terminal, with optional filtering for specific content.
   *
   * @param doc The JSoup Document representing the HTML of the page
   * @param terminal The terminal to write output to
   * @param filterArgument The filter string (optional) to match content in the page
   */
  public static void displayPageContent(Document doc, Terminal terminal, String filterArgument) {
    try {
      // Extract the title of the page and display it in bold blue
      String title = doc.title();
      terminal.writer().println(Ansi.ansi().bold().fg(Ansi.Color.BLUE).a(title).reset());

      // Extract body elements and links
      Elements bodyElements = doc.body().children();
      Elements links = doc.select("a[href]");

      // Prepare a filter pattern if the argument is not empty
      Pattern filterPattern =
          filterArgument.isEmpty()
              ? null
              : Pattern.compile(filterArgument, Pattern.CASE_INSENSITIVE);

      // Display content in the body
      for (Element element : bodyElements) {
        displayElement(element, terminal, filterPattern);
      }

      // If links exist, display them in a numbered list for navigation
      if (!links.isEmpty()) {
        terminal.writer().println("\nLinks:");
        for (int i = 0; i < links.size(); i++) {
          String linkText = links.get(i).text();
          String linkUrl = links.get(i).absUrl("href");
          terminal
              .writer()
              .println(
                  Ansi.ansi().fg(Ansi.Color.GREEN).a((i + 1) + ". " + linkText).reset()
                      + " ("
                      + linkUrl
                      + ")");
        }
      }
    } catch (Exception e) {
      // Handle errors gracefully and display them in red
      terminal
          .writer()
          .println(
              Ansi.ansi()
                  .fg(Ansi.Color.RED)
                  .a("Error displaying page content: " + e.getMessage())
                  .reset());
    }
  }

  /**
   * Displays individual elements of the page body based on their tag name. Filters content if a
   * filter pattern is provided.
   *
   * @param element The JSoup element to display
   * @param terminal The terminal to write output to
   * @param filterPattern The filter pattern (optional)
   */
  private static void displayElement(Element element, Terminal terminal, Pattern filterPattern) {
    try {
      // Skip displaying the element if it doesn't match the filter pattern
      if (filterPattern != null && !filterPattern.matcher(element.text()).find()) {
        return; // Skip if doesn't match filter
      }

      // Determine the tag name of the element and display it accordingly
      String tagName = element.tagName().toLowerCase();
      switch (tagName) {
        case "h1":
        case "h2":
        case "h3":
        case "h4":
        case "h5":
        case "h6":
          // Display headings in bold
          terminal.writer().println(Ansi.ansi().bold().a(element.text()).reset());
          break;
        case "p":
          // Display paragraphs with newline after them
          terminal.writer().println(element.text() + "\n");
          break;
        case "ul":
        case "ol":
          // Display lists, prefixed with a bullet or number
          for (Element listItem : element.children()) {
            terminal.writer().println("  * " + listItem.text());
          }
          terminal.writer().println();
          break;
        case "a":
          // Store link in history and mark as visited
          String linkUrl = element.absUrl("href");
          history.push(linkUrl); // Save the link for "back" navigation
          visitedLinks.add(linkUrl); // Mark the link as visited
          break;
        default:
          // Default case to display other types of elements
          terminal.writer().println(element.text());
      }
    } catch (Exception e) {
      // Handle errors in displaying individual elements
      terminal
          .writer()
          .println(
              Ansi.ansi()
                  .fg(Ansi.Color.RED)
                  .a("Error displaying element: " + e.getMessage())
                  .reset());
    }
  }

  /**
   * Checks if there is any history in the browsing history (for "back" navigation).
   *
   * @return true if there is at least one page in the history, false otherwise
   */
  public static boolean hasBackHistory() {
    return history.size() > 1; // We need at least two pages to go "back"
  }
}

/** Copyright © 2024 Devin B. Royal. All rights reserved. */
