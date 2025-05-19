/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SimulatedOS {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimulatedOSWindow();
        });
    }
}

class SimulatedOSWindow extends JFrame {

    private JPanel desktopPanel;
    private JTree fileTree;
    private JTextArea console;
    private JTabbedPane taskManager;
    private JFXPanel browserPanel;

    public SimulatedOSWindow() {
        setTitle("SimulatedOS - Cross of MacOS and Android");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Desktop Panel
        desktopPanel = new JPanel(null);
        desktopPanel.setBackground(new Color(50, 50, 50));
        desktopPanel.setPreferredSize(new Dimension(800, 600));
        JScrollPane desktopScrollPane = new JScrollPane(desktopPanel);
        desktopScrollPane.setBorder(BorderFactory.createTitledBorder("Desktop"));

        // File Tree
        fileTree = createFileTree();
        JScrollPane fileTreeScrollPane = new JScrollPane(fileTree);
        fileTreeScrollPane.setBorder(BorderFactory.createTitledBorder("File Manager"));

        // Console
        console = new JTextArea();
        console.setEditable(false);
        console.setBackground(new Color(20, 20, 20));
        console.setForeground(Color.GREEN);
        console.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Console"));

        // Task Manager
        taskManager = new JTabbedPane();
        taskManager.setBorder(BorderFactory.createTitledBorder("Task Manager"));

        // Browser Panel (JavaFX WebView)
        browserPanel = new JFXPanel();
        initializeBrowser();
        taskManager.addTab("Web Browser", browserPanel);

        // Layout Setup
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fileTreeScrollPane, consoleScrollPane);
        verticalSplit.setDividerLocation(400);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, verticalSplit, desktopScrollPane);
        mainSplit.setDividerLocation(300);

        add(mainSplit, BorderLayout.CENTER);
        add(taskManager, BorderLayout.SOUTH);

        initializeDesktop();
        initializeEventListeners();
        setVisible(true);
    }

    private JTree createFileTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        File[] roots = File.listRoots();
        for (File fileRoot : roots) {
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileRoot.getAbsolutePath());
            root.add(rootNode);
            loadFileSystemNodes(rootNode, fileRoot);
        }
        return new JTree(root);
    }

    private void loadFileSystemNodes(DefaultMutableTreeNode parentNode, File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getName());
                parentNode.add(node);
                if (f.isDirectory()) {
                    loadFileSystemNodes(node, f);
                }
            }
        }
    }

    private void initializeDesktop() {
        JButton launchTextEditor = new JButton("Text Editor");
        launchTextEditor.setBounds(50, 50, 120, 50);
        desktopPanel.add(launchTextEditor);

        launchTextEditor.addActionListener(e -> launchApplication("Text Editor"));
    }

    private void initializeEventListeners() {
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (node != null) {
                console.append("Selected: " + node.toString() + "\n");
            }
        });
    }

    private void initializeBrowser() {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load("https://www.google.com");
            browserPanel.setScene(new Scene(webView));
        });
    }

    private void launchApplication(String appName) {
        JPanel appPanel = new JPanel();
        appPanel.setBackground(new Color(70, 70, 70));
        appPanel.add(new JLabel("Running: " + appName));
        taskManager.addTab(appName, appPanel);
        console.append("Launching application: " + appName + "\n");
    }
}
