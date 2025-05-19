/** 
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 *
 * Advanced File Renaming and Troubleshooting Script
 * This program automatically renames files with problematic characters in a specified directory 
 * and provides advanced troubleshooting for specific errors.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.*;

public class FileRenamer {

    private static final Logger logger = Logger.getLogger(FileRenamer.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("file_rename.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.severe("Error configuring logging: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java FileRenamer <directory_path>");
            logger.severe("Invalid usage. Directory path not provided.");
            return;
        }
        Path directory = Paths.get(args[0]);
        renameFiles(directory);
        System.out.println("File renaming process completed. Check the log for details.");
    }

    public static void renameFiles(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String originalName = file.getFileName().toString();
                    String newName = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
                    if (!originalName.equals(newName)) {
                        Path newPath = file.resolveSibling(newName);
                        Files.move(file, newPath);
                        logger.info("Renamed: " + originalName + " -> " + newName);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            logger.info("File renaming completed successfully.");
        } catch (IOException e) {
            logger.severe("Error during file renaming: " + e.getMessage());
            troubleshootError(e);
        }
    }

    private static void troubleshootError(IOException error) {
        if (error instanceof AccessDeniedException) {
            logger.warning("Access Denied: Try running the script as an administrator.");
        } else if (error instanceof NoSuchFileException) {
            logger.warning("File Not Found: Ensure the directory and files exist.");
        } else {
            logger.warning("An unexpected error occurred: " + error.getMessage());
        }
    }
}

/**
 * Enhanced Logging: Added detailed logging using java.util.logging.Logger, including a console and file handler.
 *
 * Recursive File Renaming: Uses Files.walkFileTree to rename files in the specified directory and all its subdirectories.
 *
 * Advanced Error Handling: Provides detailed troubleshooting for specific errors like AccessDeniedException and NoSuchFileException.
 *
 * Usage Instructions: Checks for the correct usage and logs an error if the directory path is not provided.
 *
 * This script is now more robust and provides a comprehensive approach to renaming files and handling errors. 
 * To run this script, save it as FileRenamer.java, compile it with javac FileRenamer.java, and execute it with java FileRenamer <directory_path>.
 */
