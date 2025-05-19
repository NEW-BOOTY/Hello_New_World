/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;

public class CrossPlatformDriveInspector {

    public static void main(String[] args) {
        System.out.println("Starting Cross-Platform Drive Inspection...");

        if (isWindows()) {
            inspectWindowsDrives();
        } else if (isUnixBased()) {
            inspectUnixBasedDrives();
        } else {
            System.out.println("Unsupported operating system.");
        }

        System.out.println("Drive Inspection Completed.");
    }

    // Method to inspect drives on Windows systems
    private static void inspectWindowsDrives() {
        for (char driveLetter = 'A'; driveLetter <= 'Z'; driveLetter++) {
            String drivePath = driveLetter + ":\\";
            File drive = new File(drivePath);

            if (drive.exists() && drive.isDirectory()) {
                System.out.println("Drive " + drivePath + " exists.");
                performDriveOperations(drivePath);
            } else {
                System.out.println("Drive " + drivePath + " does not exist.");
            }
        }
    }

    // Method to inspect drives on Unix-based systems (Linux/Mac)
    private static void inspectUnixBasedDrives() {
        Path root = Paths.get("/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    System.out.println("Drive/Partition found: " + entry.toAbsolutePath());
                    performDriveOperations(entry.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error inspecting Unix-based drives: " + e.getMessage());
        }
    }

    // Perform operations on detected drives
    private static void performDriveOperations(String drivePath) {
        createAndWriteFile(drivePath);
        toggleHiddenStatus(drivePath + (isWindows() ? "\\test_file.txt" : "/test_file.txt"));
    }

    // Create and write to a file in the specified path
    private static void createAndWriteFile(String drivePath) {
        File file = new File(drivePath + (isWindows() ? "\\test_file.txt" : "/test_file.txt"));
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
            } else {
                System.out.println("File already exists: " + file.getAbsolutePath());
            }

            Files.write(file.toPath(), ("This is a test file for path: " + drivePath).getBytes());
            System.out.println("Write operation completed for: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error creating or writing to file: " + e.getMessage());
        }
    }

    // Toggle file hidden status cross-platform
    private static void toggleHiddenStatus(String filePath) {
        File file = new File(filePath);

        try {
            Path path = file.toPath();
            boolean hidden = Files.isHidden(path);

            if (hidden) {
                System.out.println("Unhiding file: " + filePath);
                if (isWindows()) {
                    Files.setAttribute(path, "dos:hidden", false);
                } else {
                    setUnixFileHidden(path, false);
                }
            } else {
                System.out.println("Hiding file: " + filePath);
                if (isWindows()) {
                    Files.setAttribute(path, "dos:hidden", true);
                } else {
                    setUnixFileHidden(path, true);
                }
            }
        } catch (IOException e) {
            System.err.println("Error toggling hidden status: " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            System.out.println("Hiding/unhiding not supported on this platform for: " + filePath);
        }
    }

    // Set hidden attribute on Unix-based systems
    private static void setUnixFileHidden(Path path, boolean hide) throws IOException {
        Path fileName = path.getFileName();
        if (fileName != null) {
            Path newFileName = hide ? path.resolveSibling("." + fileName) : path.resolveSibling(fileName.toString().replaceFirst("^\\.", ""));
            Files.move(path, newFileName, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Helper methods to detect operating system
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static boolean isUnixBased() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).matches(".*(nix|nux|mac).*");
    }
}


/*
 * Features:
 * 
 * User Input:
 * - Prompts the user to enter the directory path.
 * 
 * File Validation:
 * - Ensures the directory exists and contains .java files.
 * 
 * Java Compiler API:
 * - Utilizes the ToolProvider.getSystemJavaCompiler() to compile files programmatically.
 * 
 * Error Handling:
 * - Handles exceptions for invalid input, directory access, and compilation errors.
 * 
 * Output Messages:
 * - Provides clear feedback on success or failure of each file's compilation.
 * 
 * How to Run:
 * - Save the program as DirectoryJavaCompiler.java.
 * - Compile the program using:
 *   javac DirectoryJavaCompiler.java
 * - Run the program:
 *   java DirectoryJavaCompiler
 * 
 * This will compile all .java files in the specified directory. 
 * Make sure the JAVA_HOME environment variable points to your JDK installation.
 */

/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
