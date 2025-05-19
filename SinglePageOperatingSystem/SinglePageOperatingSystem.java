/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.io.*;
import java.util.*;

public class SinglePageOperatingSystem {

  // Core components
  private final List<User> users = new ArrayList<>();
  private final List<File> files = new ArrayList<>();

  // System status
  private boolean isRunning = false;

  // Main entry point
  public static void main(String[] args) {
    SinglePageOperatingSystem os = new SinglePageOperatingSystem();
    os.run();
  }

  // Run the Operating System
  public void run() {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.println("=== Welcome to Single-Page OS ===");
      isRunning = true;

      while (isRunning) {
        try {
          System.out.println("\nChoose an operation:");
          System.out.println("1. Add User");
          System.out.println("2. Create File");
          System.out.println("3. List Files");
          System.out.println("4. Execute Program");
          System.out.println("5. Shutdown");
          System.out.print("Enter your choice: ");
          int choice = Integer.parseInt(scanner.nextLine());

          switch (choice) {
            case 1 -> addUser(scanner);
            case 2 -> createFile(scanner);
            case 3 -> listFiles();
            case 4 -> executeProgram(scanner);
            case 5 -> shutdown();
            default -> System.out.println("Invalid choice. Please try again.");
          }
        } catch (NumberFormatException e) {
          System.out.println("[Error] Invalid input. Please enter a number.");
        } catch (Exception e) {
          System.out.println("[Error] An unexpected error occurred: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      System.out.println("[Error] Fatal error: " + e.getMessage());
    }
  }

  // Add a new user
  private void addUser(Scanner scanner) {
    System.out.print("Enter user name: ");
    String name = scanner.nextLine().trim();

    if (name.isEmpty()) {
      System.out.println("[Error] User name cannot be empty.");
      return;
    }

    if (users.stream().anyMatch(user -> user.getName().equals(name))) {
      System.out.println("[Error] A user with this name already exists.");
      return;
    }

    User user = new User(name);
    users.add(user);
    System.out.println("User added: " + name);
  }

  // Create a new file
  private void createFile(Scanner scanner) {
    System.out.print("Enter file name: ");
    String fileName = scanner.nextLine().trim();

    if (fileName.isEmpty()) {
      System.out.println("[Error] File name cannot be empty.");
      return;
    }

    if (files.stream().anyMatch(file -> file.getName().equals(fileName))) {
      System.out.println("[Error] A file with this name already exists.");
      return;
    }

    Permission permission = selectPermission(scanner);
    if (permission == null) {
      return;
    }

    File file = new File(fileName, permission);
    files.add(file);
    System.out.println("File created: " + fileName + " with " + permission + " permission.");
  }

  // Select a permission for the file
  private Permission selectPermission(Scanner scanner) {
    System.out.println("Select permission:");
    System.out.println("1. READ");
    System.out.println("2. WRITE");
    System.out.println("3. EXECUTE");

    try {
      int permissionChoice = Integer.parseInt(scanner.nextLine());
      return switch (permissionChoice) {
        case 1 -> Permission.READ;
        case 2 -> Permission.WRITE;
        case 3 -> Permission.EXECUTE;
        default -> throw new IllegalArgumentException("Invalid permission choice.");
      };
    } catch (NumberFormatException e) {
      System.out.println("[Error] Please enter a valid number for permission.");
    } catch (IllegalArgumentException e) {
      System.out.println("[Error] " + e.getMessage());
    }

    return null;
  }

  // List all files
  private void listFiles() {
    if (files.isEmpty()) {
      System.out.println("No files available.");
    } else {
      System.out.println("Files:");
      for (File file : files) {
        System.out.println("- " + file.getName() + " (" + file.getPermission() + ")");
      }
    }
  }

  // Execute a program
  private void executeProgram(Scanner scanner) {
    System.out.print("Enter program name to execute: ");
    String programName = scanner.nextLine().trim();

    if (programName.isEmpty()) {
      System.out.println("[Error] Program name cannot be empty.");
      return;
    }

    System.out.println("Executing program: " + programName);
    System.out.println("Program " + programName + " executed successfully.");
  }

  // Shutdown the OS
  private void shutdown() {
    System.out.println("Shutting down the OS...");
    isRunning = false;
  }

  // User class
  static class User {
    private final String name;

    public User(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  // File class
  static class File {
    private final String name;
    private final Permission permission;

    public File(String name, Permission permission) {
      this.name = name;
      this.permission = permission;
    }

    public String getName() {
      return name;
    }

    public Permission getPermission() {
      return permission;
    }
  }

  // Permission enum
  enum Permission {
    READ,
    WRITE,
    EXECUTE
  }
}

/*
 * Features
 * ==========
 * User Management:
 * - Add users with unique names.
 * - Store user details in a list for retrieval.
 *
 * File Management:
 * - Create files with specific permissions (READ, WRITE, EXECUTE).
 * - List all created files with details.
 *
 * Program Execution:
 * - Simulate executing a named program.
 * - Display execution success messages.
 *
 * System Control:
 * - Shutdown option to terminate the program safely.
 *
 * Error Handling:
 * - Prevents crashes due to invalid input.
 * - Provides default values for incorrect choices.
 *
 * How to Run
 * ==========
 * 1. Save the program as SinglePageOperatingSystem.java.
 * 2. Compile the program:
 *    javac SinglePageOperatingSystem.java
 * 3. Run the program:
 *    java SinglePageOperatingSystem
 *
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
