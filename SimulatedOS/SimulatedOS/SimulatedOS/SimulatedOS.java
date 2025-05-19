/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SimulatedOS {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulatedOSWindow window = new SimulatedOSWindow();
            window.setVisible(true);
        });
    }
}

class SimulatedOSWindow extends JFrame {
    private JTree fileSystemTree;
    private JEditorPane browserPane;

    public SimulatedOSWindow() {
        setTitle("Simulated OS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // File Manager
        fileSystemTree = createFileManager();
        JScrollPane fileScrollPane = new JScrollPane(fileSystemTree);
        fileScrollPane.setPreferredSize(new Dimension(200, 600));
        add(fileScrollPane, BorderLayout.WEST);

        // Browser Panel
        browserPane = new JEditorPane();
        browserPane.setEditable(false);
        browserPane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    browserPane.setPage(event.getURL());
                } catch (IOException e) {
                    showError("Failed to load URL: " + event.getURL());
                }
            }
        });

        JScrollPane browserScrollPane = new JScrollPane(browserPane);
        add(browserScrollPane, BorderLayout.CENTER);

        // Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        JTextField urlField = new JTextField("https://www.google.com");
        JButton goButton = new JButton("Go");
        goButton.addActionListener(e -> loadURL(urlField.getText()));
        navBar.add(urlField, BorderLayout.CENTER);
        navBar.add(goButton, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        loadURL("https://www.google.com");
    }

    private JTree createFileManager() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode home = new DefaultMutableTreeNode("Home");
        DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
        DefaultMutableTreeNode downloads = new DefaultMutableTreeNode("Downloads");

        root.add(home);
        home.add(documents);
        home.add(downloads);

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        return new JTree(treeModel);
    }

    private void loadURL(String url) {
        try {
            browserPane.setPage(url);
        } catch (IOException e) {
            showError("Invalid URL or failed to load: " + url);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

/*
 * This program simulates a minimal operating system with the following components:
 * 1. File Manager: A hierarchical tree structure to navigate directories.
 * 2. Web Browser: A Swing-based browser using JEditorPane to load and display web pages.
 * 3. Navigation Bar: Allows users to enter URLs and navigate the web.
 * All functionalities are fully integrated into a single JFrame with proper error handling.
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
