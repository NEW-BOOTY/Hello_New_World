/** Copyright © 2024 Devin B. Royal. All rights reserved. */
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 *
 * <p>Provides methods to manage system-level permissions for files and directories.
 */
public class SystemAccessManager {

  private static final Logger logger = Logger.getLogger(SystemAccessManager.class.getName());
  private static Properties config = new Properties();
  private static String permissionMode;
  private static String logFilePath;

  static {
    // Configure logging
    try {
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(Level.ALL);
      logger.addHandler(consoleHandler);

      // Set up file logging if specified in config
      logFilePath = config.getProperty("logFilePath");
      if (logFilePath != null && !logFilePath.isEmpty()) {
        FileHandler fileHandler = new FileHandler(logFilePath, true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
      }

      logger.setLevel(Level.ALL);

      // Load configuration
      loadConfiguration();
    } catch (IOException e) {
      logger.severe("Error configuring logging: " + e.getMessage());
    }
  }

  /** Loads the configuration from the properties file. */
  private static void loadConfiguration() {
    try (InputStream input = new FileInputStream("config.properties")) {
      // Validate if the config file exists before trying to load
      File configFile = new File("config.properties");
      if (!configFile.exists()) {
        throw new FileNotFoundException("Configuration file not found.");
      }
      config.load(input);
      permissionMode = config.getProperty("permissionMode", "777"); // Default to full access
      validatePermissionMode(permissionMode);
    } catch (IOException | IllegalArgumentException e) {
      logger.warning(
          "Failed to load configuration or invalid permission mode. Using default: 777.");
      permissionMode = "777"; // Fall back to default permission
    }
  }

  /**
   * Validates the permission mode format.
   *
   * @param mode The permission mode to validate.
   * @throws IllegalArgumentException If the mode is not valid.
   */
  private static void validatePermissionMode(String mode) throws IllegalArgumentException {
    if (!mode.matches("\\d{3}")) {
      throw new IllegalArgumentException("Invalid permission mode format. Must be three digits.");
    }
  }

  /**
   * Grants full functional use of a file or directory by adjusting its access policies. This method
   * attempts to modify permissions based on the operating system.
   *
   * @param pathString The path of the file or directory.
   * @throws IOException If an I/O error occurs.
   */
  public static void grantFullFunctionalUse(String pathString) throws IOException {
    Path path = Paths.get(pathString);

    if (Files.exists(path)) {
      if (!Files.isWritable(path.getParent())) {
        throw new SecurityException(
            "No write permissions to parent directory: " + path.getParent());
      }

      String osName = System.getProperty("os.name").toLowerCase();
      if (osName.contains("win")) {
        // Windows-specific command
        runCommandWindows(pathString);
      } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
        // Unix-like systems (Linux, macOS)
        runCommandUnix(pathString);
      } else {
        throw new UnsupportedOperationException("Unsupported operating system: " + osName);
      }
    } else {
      throw new IllegalArgumentException("The specified path does not exist: " + pathString);
    }
  }

  /**
   * Executes a Windows-specific permission change command using `icacls`.
   *
   * @param pathString The path of the file or directory.
   * @throws IOException If an I/O error occurs.
   */
  private static void runCommandWindows(String pathString) throws IOException {
    String command = "icacls \"" + pathString + "\" /grant Everyone:(OI)(CI)F /T";
    runCommand(command);
  }

  /**
   * Executes a Unix-based permission change command using `chmod`.
   *
   * @param pathString The path of the file or directory.
   * @throws IOException If an I/O error occurs.
   */
  private static void runCommandUnix(String pathString) throws IOException {
    String command = "chmod -R " + permissionMode + " \"" + pathString + "\"";
    runCommand(command);
  }

  /**
   * Prompts the user for confirmation before applying changes.
   *
   * @param pathString The path of the file or directory.
   * @return true if the user confirms, false otherwise.
   */
  private static boolean promptUserConfirmation(String pathString) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println(
          "Are you sure you want to change permissions for: " + pathString + "? (yes/no)");
      String response = scanner.nextLine().trim().toLowerCase();
      if (response.equals("yes")) {
        return true;
      } else if (response.equals("no")) {
        return false;
      } else {
        System.out.println("Invalid response. Please enter 'yes' or 'no'.");
      }
    }
  }

  /**
   * Executes a system command and logs the output.
   *
   * @param command The command to execute.
   * @throws IOException If an I/O error occurs.
   */
  private static void runCommand(String command) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    processBuilder.redirectErrorStream(true);

    Process process = processBuilder.start();

    // Log output and errors
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        logger.info(line);
      }

      try {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          throw new IOException("Command failed with exit code: " + exitCode);
        }
        logger.info("Command executed successfully: " + command);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Command execution interrupted", e);
      }
    }
  }

  /**
   * Recursively changes permissions for all files and directories within a given directory.
   *
   * @param directoryPath The path of the directory.
   * @throws IOException If an I/O error occurs.
   */
  private static void changePermissionsRecursively(Path directoryPath) throws IOException {
    Files.walkFileTree(
        directoryPath,
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            try {
              if (Files.isSymbolicLink(file)) {
                logger.warning("Skipping symbolic link: " + file);
              } else {
                runCommandUnix(file.toString());
              }
            } catch (IOException e) {
              logger.severe(
                  "Failed to change permissions for file: " + file + " Error: " + e.getMessage());
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null) {
              logger.severe("Error visiting directory: " + dir + " Error: " + exc.getMessage());
            } else {
              try {
                runCommandUnix(dir.toString());
              } catch (IOException e) {
                logger.severe(
                    "Failed to change permissions for directory: "
                        + dir
                        + " Error: "
                        + e.getMessage());
              }
            }
            return FileVisitResult.CONTINUE;
          }
        });
  }

  /**
   * Main method to execute the permission changes.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java SystemAccessManager <path>");
      return;
    }

    String pathString = args[0];

    try {
      if (promptUserConfirmation(pathString)) {
        grantFullFunctionalUse(pathString);
        logger.info("Full functional use granted.");
      } else {
        logger.info("Operation cancelled by the user.");
      }
    } catch (IOException
        | SecurityException
        | UnsupportedOperationException
        | IllegalArgumentException e) {
      logger.severe("Error granting full functional use: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Unexpected error: " + e.getMessage());
    } finally {
      // Ensure any necessary cleanup or resource deallocation is handled
      logger.info("Process terminated.");
    }
  }
}
