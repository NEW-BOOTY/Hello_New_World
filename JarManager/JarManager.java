/*
 * Copyright © 2025 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.zip.*;
import java.util.*;
import java.util.logging.*;

public class JarManager {
    private static final String JAR_DIRECTORY = "./jars";
    private static final Logger LOGGER = Logger.getLogger(JarManager.class.getName());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        setupLogging();
        File jarDir = new File(JAR_DIRECTORY);
        if (!jarDir.exists()) jarDir.mkdirs();
        
        EXECUTOR.execute(() -> monitorJarDirectory(jarDir));
        EXECUTOR.execute(() -> listJarFiles(jarDir));
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

    private static void listJarFiles(File jarDir) {
        File[] jars = jarDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            LOGGER.info("No JAR files found in " + jarDir.getAbsolutePath());
            return;
        }
        for (File jar : jars) {
            EXECUTOR.execute(() -> {
                try {
                    listJarContents(jar);
                } catch (IOException e) {
                    LOGGER.severe("Error listing contents of " + jar.getName() + ": " + e.getMessage());
                }
            });
        }
    }

    private static void listJarContents(File jarFile) throws IOException {
        LOGGER.info("Listing contents of: " + jarFile.getName());
        try (ZipFile zip = new ZipFile(jarFile)) {
            zip.stream().forEach(entry -> LOGGER.info("  - " + entry.getName()));
        }
    }

    private static void executeJar(File jarFile) {
        EXECUTOR.execute(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath());
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                pb.start();
                LOGGER.info("Executing " + jarFile.getName());
            } catch (IOException e) {
                LOGGER.severe("Execution failed for " + jarFile.getName() + ": " + e.getMessage());
            }
        });
    }

    private static void monitorJarDirectory(File jarDir) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = jarDir.toPath();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            LOGGER.info("Monitoring " + jarDir.getAbsolutePath() + " for changes...");

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = path.resolve((Path) event.context());
                    File changedFile = changed.toFile();
                    LOGGER.info("Detected " + event.kind() + " on " + changedFile.getName());
                    if (changedFile.getName().endsWith(".jar")) {
                        executeJar(changedFile);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Directory monitoring failed: " + e.getMessage());
        }
    }
}

/**
 * This production-ready Java program:
 * 
 * - Scans & Lists JAR files in ./jars
 * - Extracts and displays contents
 * - Executes JARs dynamically
 * - Monitors changes in real time
 * - Logs all operations
 * 
 * It uses multi-threading, error handling, and directory watching for efficiency.
 */
