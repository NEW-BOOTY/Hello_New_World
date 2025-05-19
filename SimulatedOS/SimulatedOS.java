/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 * 
 * Unauthorized use, distribution, or reproduction of this code and/or software is prohibited without written consent from the author. 
 * If another entity, person, corporation, or organization profits from this creation, software, and/or code, 
 * then the profit must be split 50/50 with the author. Any further sharing must also adhere to these terms. 
 * For any questions, please contact the author.
 * Email: PAY_ME@MY.COM; JAVA-DEVELOPER@PROGRAMMER.NET
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.tree.DefaultMutableTreeNode;


public class SimulatedOS {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OSWindow osWindow = new OSWindow();
            osWindow.setVisible(true);
        });
    }
}

class OSWindow extends JFrame {

    private JPanel desktop;
    private JTree fileSystemTree;
    private JTextArea console;
    private JTabbedPane taskManager;

    public OSWindow() {
        setTitle("SimulatedOS - Cross of MacOS and Android");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Desktop Panel
        desktop = new JPanel();
        desktop.setLayout(null);
        desktop.setBackground(new Color(30, 30, 30));
        desktop.setPreferredSize(new Dimension(800, 600));
        JScrollPane desktopScroll = new JScrollPane(desktop);
        desktopScroll.setBorder(BorderFactory.createTitledBorder("Desktop"));

        // File System Simulation
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode home = new DefaultMutableTreeNode("Home");
        DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
        DefaultMutableTreeNode downloads = new DefaultMutableTreeNode("Downloads");
        home.add(documents);
        home.add(downloads);
        root.add(home);
        fileSystemTree = new JTree(root);
        JScrollPane fileTreeScroll = new JScrollPane(fileSystemTree);
        fileTreeScroll.setBorder(BorderFactory.createTitledBorder("File System"));

        // Console Panel
        console = new JTextArea();
        console.setEditable(false);
        console.setBackground(new Color(10, 10, 10));
        console.setForeground(Color.GREEN);
        console.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane consoleScroll = new JScrollPane(console);
        consoleScroll.setBorder(BorderFactory.createTitledBorder("Console"));

        // Task Manager
        taskManager = new JTabbedPane();
        taskManager.setBorder(BorderFactory.createTitledBorder("Task Manager"));

        // Layout Components
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fileTreeScroll, consoleScroll);
        leftSplit.setDividerLocation(300);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, desktopScroll);
        mainSplit.setDividerLocation(300);

        add(mainSplit, BorderLayout.CENTER);
        add(taskManager, BorderLayout.SOUTH);

        initializeDesktop();
        initializeEventListeners();
    }

    private void initializeDesktop() {
        JButton appLauncher = new JButton("App Launcher");
        appLauncher.setBounds(50, 50, 150, 50);
        desktop.add(appLauncher);

        appLauncher.addActionListener(e -> {
            String appName = JOptionPane.showInputDialog(this, "Enter App Name:", "Launch App", JOptionPane.PLAIN_MESSAGE);
            if (appName != null && !appName.trim().isEmpty()) {
                launchApplication(appName);
            }
        });
    }

    private void initializeEventListeners() {
        fileSystemTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getLastSelectedPathComponent();
            if (node != null) {
                console.append("Selected: " + node.toString() + "\n");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                console.append("Shutting down SimulatedOS...\n");
            }
        });
    }

    private void launchApplication(String appName) {
        JPanel appPanel = new JPanel();
        appPanel.setBackground(new Color(60, 60, 60));
        appPanel.add(new JLabel("Running: " + appName));
        taskManager.addTab(appName, appPanel);
        console.append("Launching application: " + appName + "\n");
    }
}
