/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TerminalWebBrowser {
    private static List<String> history = new ArrayList<>();
    private static List<String> bookmarks = new ArrayList<>();
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            while (true) {
                String input = reader.readLine("Enter URL (or 'exit' to quit, 'history' to view history, 'bookmarks' to view bookmarks, 'search' to search text, 'useragent' to set User-Agent): ");
                if (input.equalsIgnoreCase("exit")) {
                    break;
                } else if (input.equalsIgnoreCase("history")) {
                    displayHistory(terminal);
                    continue;
                } else if (input.equalsIgnoreCase("bookmarks")) {
                    displayBookmarks(terminal);
                    continue;
                } else if (input.equalsIgnoreCase("search")) {
                    searchInHistory(reader, terminal);
                    continue;
                } else if (input.equalsIgnoreCase("useragent")) {
                    setUserAgent(reader, terminal);
                    continue;
                }

                try {
                    Document doc = Jsoup.connect(input).userAgent(userAgent).get();
                    String title = doc.title();
                    String body = doc.body().text();
                    String css = parseCSS(doc);

                    terminal.writer().println("Title: " + title);
                    terminal.writer().println("Content: " + body);
                    terminal.writer().println("CSS: " + css);

                    history.add(input);
                } catch (IOException e) {
                    terminal.writer().println("Failed to retrieve content from the URL: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayHistory(Terminal terminal) {
        terminal.writer().println("Browsing History:");
        for (String url : history) {
            terminal.writer().println(url);
        }
    }

    private static void displayBookmarks(Terminal terminal) {
        terminal.writer().println("Bookmarks:");
        for (String url : bookmarks) {
            terminal.writer().println(url);
        }
    }

    private static void searchInHistory(LineReader reader, Terminal terminal) {
        String searchTerm = reader.readLine("Enter search term: ");
        terminal.writer().println("Search Results:");
        for (String url : history) {
            if (url.contains(searchTerm)) {
                terminal.writer().println(url);
            }
        }
    }

    private static void setUserAgent(LineReader reader, Terminal terminal) {
        userAgent = reader.readLine("Enter new User-Agent: ");
        terminal.writer().println("User-Agent set to: " + userAgent);
    }

    private static String parseCSS(Document doc) {
        Elements styles = doc.select("style");
        StringBuilder css = new StringBuilder();
        for (Element style : styles) {
            css.append(style.html()).append("\n");
        }
        return css.toString();
    }
}

