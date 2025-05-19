/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */


import java.io.File;
import java.io.IOException;

public class DriveInspector {

    public static void main(String[] args) {
        System.out.println("Starting Drive Inspection from A: to Z:...");

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

        System.out.println("Drive Inspection Completed.");
    }

    // Method to perform operations on a valid drive
    private static void performDriveOperations(String drivePath) {
        changeDirectory(drivePath);
        createAndWriteFile(drivePath);
        toggleFileHiddenStatus(drivePath + "test_file.txt");
    }

    // Change directory operation (simulated in Java)
    private static void changeDirectory(String drivePath) {
        System.out.println("Changing directory to: " + drivePath);
        // In Java, direct changing of directory contexts is not typical but simulated with operations in the target directory.
    }

    // Create a file and write to it on the specified drive
    private static void createAndWriteFile(String drivePath) {
        File file = new File(drivePath + "test_file.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
            } else {
                System.out.println("File already exists: " + file.getAbsolutePath());
            }

            // Write to the file
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("This is a test file for drive: " + drivePath);
                System.out.println("Write operation completed for: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating or writing to file: " + e.getMessage());
        }
    }

    // Toggle file hidden status
    private static void toggleFileHiddenStatus(String filePath) {
        File file = new File(filePath);

        try {
            boolean hidden = file.isHidden();
            String command = hidden ? "attrib -H " : "attrib +H ";
            command += filePath;

            Process process = Runtime.getRuntime().exec("cmd /c " + command);
            process.waitFor();
            System.out.println((hidden ? "Unhidden" : "Hidden") + " file: " + filePath);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error toggling hidden status: " + e.getMessage());
        }
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
