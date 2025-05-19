/*
 * Copyright (c) 2024 Devin B. Royal. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomBrowser {

    private JFrame frame;
    private JTextField addressBar;
    private JButton goButton;
    private JFXPanel jfxPanel;
    private WebView webView;
    private WebEngine webEngine;

    public CustomBrowser() {
        // Set up the JFrame window
        frame = new JFrame("Custom Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLayout(new BorderLayout());

        // Create address bar and go button
        addressBar = new JTextField();
        goButton = new JButton("Go");

        // Set up the WebView with JavaFX
        jfxPanel = new JFXPanel();  // Initializes the JavaFX runtime
        frame.add(jfxPanel, BorderLayout.CENTER);

        // Set up top panel with address bar and go button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(addressBar, BorderLayout.CENTER);
        topPanel.add(goButton, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        // Initialize JavaFX WebView and WebEngine on the JFXPanel
        Platform.runLater(() -> initializeJavaFX());

        // ActionListener for the Go button
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = addressBar.getText();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url; // Default to HTTP
                }
                loadPage(url);
            }
        });

        frame.setVisible(true);
    }

    private void initializeJavaFX() {
        // Set up the WebView and WebEngine
        webView = new WebView();
        webEngine = webView.getEngine();

        // Create a Scene for the JavaFX content
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }

    private void loadPage(String url) {
        // Load the URL in the WebView using WebEngine
        Platform.runLater(() -> {
            webEngine.load(url);
        });
    }

    public static void main(String[] args) {
        // Run the browser setup on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new CustomBrowser();
        });
    }
}

/*
 * Copyright (c) 2024 Devin B. Royal. All Rights Reserved.
 */

/*
 * Key Features of the Program:
 * ============================
 * 
 * 1. Basic Web Browser UI:
 *    - Creates a window with a JTextField for URL input and a JButton to navigate to the specified URL.
 *    - Uses JavaFX WebView to display the web page within the application window.
 * 
 * 2. JavaFX Integration:
 *    - Utilizes JFXPanel to embed JavaFX components in a Swing application.
 *    - Employs JavaFX WebView and WebEngine for rendering web content.
 *    - Ensures thread safety by using Platform.runLater() to manipulate JavaFX components on the JavaFX Application thread.
 * 
 * 3. Event Handling:
 *    - The Go button has an ActionListener to read the URL from the address bar and load the corresponding web page in the WebView.
 *    - Automatically appends "http://" to URLs if they don't include "http://" or "https://".
 * 
 * 4. Swing and JavaFX Interaction:
 *    - Integrates Swing (JFrame, JPanel, JTextField, JButton) for the primary UI components.
 *    - Leverages JavaFX (WebView, WebEngine, JFXPanel) for web page rendering within the application.
 * 
 * Features You Can Add or Extend:
 * ===============================
 * 
 * 1. Back and Forward Navigation:
 *    - Add navigation buttons for going back, forward, refreshing, and returning to a home page.
 * 
 * 2. Bookmarks and History:
 *    - Implement bookmarking functionality to save frequently visited URLs.
 *    - Track and display browsing history for user reference.
 * 
 * 3. Error Handling:
 *    - Gracefully handle errors such as invalid URLs or failed web page loads.
 *    - Display error messages or placeholder content when web pages cannot be loaded.
 * 
 * 4. Additional UI Improvements:
 *    - Add features like loading indicators, progress bars, or support for multiple browser tabs.
 * 
 * 5. Security Enhancements:
 *    - Integrate security features such as blocking certain domains.
 *    - Provide support for handling cookies, sessions, or other web security measures.
 * 
 * What the Program Does:
 * ======================
 * 
 * - Loads web pages in a basic graphical interface combining Java Swing and JavaFX.
 * - Users can enter a URL into the address bar and click the "Go" button to view the page in the embedded WebView.
 * 
 * What It Can Be Used For:
 * ========================
 * 
 * 1. Basic Web Browser:
 *    - Functions as a minimalistic web browser for displaying web pages.
 *    - Serves as a starting point for developing more feature-rich browsers.
 * 
 * 2. Educational Tool:
 *    - Demonstrates the integration of Swing and JavaFX in a single application.
 *    - Useful for learning how to combine different Java frameworks and libraries effectively.
 * 
 * 3. Customizable Browser:
 *    - Provides a foundation for creating specialized browsers for tasks such as testing websites or secure browsing.
 *    - Can be extended with custom extensions and functionality tailored to specific use cases.
 * 
 * Summary:
 * ========
 * 
 * - This program offers a simple, functional web browser built with Java Swing and JavaFX.
 * - It serves as a practical example of integrating Java libraries to create a customizable application.
 * - The features and architecture allow for significant customization and extension for advanced web navigation and security functionalities.
 */
