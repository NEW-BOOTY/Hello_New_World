/*
 * Copyright © 2025 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.zip.*;
import java.util.*;
import java.util.logging.*;
import java.net.*;
import com.sun.net.httpserver.*;
import javax.swing.*;

public class JarManager {
    private static final String JAR_DIRECTORY = "./jars";
    private static final Logger LOGGER = Logger.getLogger(JarManager.class.getName());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Map<String, Process> runningJars = new ConcurrentHashMap<>();
    private static boolean monitorEnabled = true;

    public static void main(String[] args) {
        setupLogging();
        File jarDir = new File(JAR_DIRECTORY);
        if (!jarDir.exists()) jarDir.mkdirs();
        
        EXECUTOR.execute(() -> startHttpServer());
        EXECUTOR.execute(() -> monitorJarDirectory(jarDir));
        EXECUTOR.execute(() -> showGui());
        userInterface(jarDir);
    }

    private static void setupLogging() {
        try {
            FileHandler fh = new FileHandler("jar_manager.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Logging setup failed: " + e.getMessage());
        }
    }

    private static void userInterface(File jarDir) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. List JARs\n2. View JAR Contents\n3. Execute JAR\n4. Extract JAR\n5. Stop Running JAR\n6. Toggle Monitoring\n7. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": listJarFiles(jarDir); break;
                case "2": System.out.print("Enter JAR name: "); listJarContents(new File(jarDir, scanner.nextLine())); break;
                case "3": System.out.print("Enter JAR name: "); executeJar(new File(jarDir, scanner.nextLine()), null); break;
                case "4": System.out.print("Enter JAR name: "); extractJar(new File(jarDir, scanner.nextLine())); break;
                case "5": System.out.print("Enter JAR name: "); stopJar(scanner.nextLine()); break;
                case "6": monitorEnabled = !monitorEnabled; break;
                case "7": System.exit(0);
                default: System.out.println("Invalid choice");
            }
        }
    }

    private static void listJarFiles(File jarDir) {
        File[] jars = jarDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jars == null || jars.length == 0) LOGGER.info("No JAR files found");
        else for (File jar : jars) System.out.println("- " + jar.getName());
    }

    private static void listJarContents(File jarFile) {
        try (ZipFile zip = new ZipFile(jarFile)) {
            zip.stream().forEach(entry -> System.out.println("  - " + entry.getName()));
        } catch (IOException e) {
            LOGGER.severe("Error reading JAR: " + e.getMessage());
        }
    }

    private static void executeJar(File jarFile, String args) {
        EXECUTOR.execute(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath());
                if (args != null) pb.command().addAll(Arrays.asList(args.split(" ")));
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process process = pb.start();
                runningJars.put(jarFile.getName(), process);
            } catch (IOException e) {
                LOGGER.severe("Execution failed: " + e.getMessage());
            }
        });
    }

    private static void stopJar(String jarName) {
        Process process = runningJars.remove(jarName);
        if (process != null) process.destroy();
        else System.out.println("JAR not running");
    }

    private static void extractJar(File jarFile) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile))) {
            File extractDir = new File(jarFile.getParent(), jarFile.getName().replace(".jar", "_extracted"));
            extractDir.mkdirs();
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(extractDir, entry.getName());
                if (entry.isDirectory()) newFile.mkdirs();
                else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        fos.write(zis.readAllBytes());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Extraction failed: " + e.getMessage());
        }
    }

    private static void monitorJarDirectory(File jarDir) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = jarDir.toPath();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            while (monitorEnabled) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    File changedFile = new File(jarDir, event.context().toString());
                    if (changedFile.getName().endsWith(".jar")) executeJar(changedFile, null);
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Monitoring failed: " + e.getMessage());
        }
    }

    private static void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/status", exchange -> {
                String response = "Running JARs: " + runningJars.keySet();
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            });
            server.start();
        } catch (IOException e) {
            LOGGER.severe("HTTP Server failed: " + e.getMessage());
        }
    }

    private static void showGui() {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "JAR Manager Running..."));
    }
}
