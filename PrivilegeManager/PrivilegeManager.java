/** Copyright © 2024 Devin B. Royal. All rights reserved. */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.*;

/**
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 *
 * <p>Provides methods to attempt gaining administrative or root privileges on the system. Enhanced
 * with features for better control, security, and user interaction.
 */
public class PrivilegeManager {

  private static final Logger logger = Logger.getLogger(PrivilegeManager.class.getName());
  private static final String CONFIG_FILE = "config.properties";
  private static String commandToRun;
  private static boolean logToFile;
  private static int maxRetries;
  private static long retryDelay;
  private static boolean verboseLogging;
  private static boolean dryRun;
  private static String emailNotification;
  private static boolean logCommandOutput;

  static {
    // Configure logging with a rotating file handler
    try {
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(Level.ALL);
      logger.addHandler(consoleHandler);

      Properties config = new Properties();
      try (InputStream input = new FileInputStream(CONFIG_FILE)) {
        config.load(input);
        String logFilePath = config.getProperty("logFilePath");
        logToFile = Boolean.parseBoolean(config.getProperty("logToFile", "false"));
        maxRetries = Integer.parseInt(config.getProperty("maxRetries", "3"));
        retryDelay = Long.parseLong(config.getProperty("retryDelay", "1000"));
        verboseLogging = Boolean.parseBoolean(config.getProperty("verboseLogging", "false"));
        dryRun = Boolean.parseBoolean(config.getProperty("dryRun", "false"));
        emailNotification = config.getProperty("emailNotification");
        logCommandOutput = Boolean.parseBoolean(config.getProperty("logCommandOutput", "true"));

        if (logToFile && logFilePath != null && !logFilePath.isEmpty()) {
          FileHandler fileHandler = new FileHandler(logFilePath, true);
          fileHandler.setFormatter(new SimpleFormatter());
          logger.addHandler(fileHandler);
        }
        logger.setLevel(Level.ALL);
        commandToRun = config.getProperty("commandToRun", "echo 'No command specified'");
      } catch (IOException e) {
        logger.warning("Failed to load configuration. Using default settings.");
        commandToRun = "echo 'No command specified'";
      }
    } catch (IOException e) {
      logger.severe("Error configuring logging: " + e.getMessage());
    }
  }

  /**
   * Attempts to gain administrative or root privileges for the current process. This method varies
   * depending on the operating system.
   *
   * @throws IOException If an I/O error occurs during command execution.
   * @throws UnsupportedOperationException If the operating system is unsupported.
   */
  private static void gainAdminAndRootPrivileges() throws IOException {
    if (dryRun) {
      logger.info("Dry run enabled. Command would be: " + commandToRun);
      return;
    }

    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      // Windows-specific command to run as administrator
      runCommand(
          "powershell -Command \"Start-Process cmd -ArgumentList '/c', '"
              + commandToRun
              + "' -Verb RunAs\"");
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
      // Unix-like systems (Linux, macOS) command to run as root
      runCommand("sudo -s " + commandToRun);
    } else {
      throw new UnsupportedOperationException("Unsupported operating system: " + osName);
    }
  }

  /**
   * Executes a system command and logs the output and errors.
   *
   * @param command The command to execute.
   * @throws IOException If an I/O error occurs during command execution.
   */
  private static void runCommand(String command) throws IOException {
    int attempt = 0;
    while (attempt < maxRetries) {
      try {
        if (verboseLogging) {
          logger.info("Executing command: " + command);
        }

        Process process = Runtime.getRuntime().exec(command);

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

          String line;
          while ((line = reader.readLine()) != null) {
            if (logCommandOutput) {
              logger.info(line);
            }
          }

          while ((line = errorReader.readLine()) != null) {
            logger.severe(line);
          }

          int exitCode = process.waitFor();
          if (exitCode == 0) {
            logger.info("Command executed successfully: " + command);
            sendEmailNotification("Command executed successfully: " + command);
            return;
          } else {
            throw new IOException("Command failed with exit code: " + exitCode);
          }
        }
      } catch (IOException | InterruptedException e) {
        logger.severe("Attempt " + (attempt + 1) + " failed: " + e.getMessage());
        attempt++;
        if (attempt < maxRetries) {
          logger.info("Retrying in " + retryDelay + " milliseconds...");
          try {
            Thread.sleep(retryDelay);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IOException("Retry interrupted", ie);
          }
        } else {
          throw new IOException("Maximum retries reached. Command failed.", e);
        }
      }
    }
  }

  /**
   * Prompts the user for confirmation before executing the privilege escalation command.
   *
   * @return true if the user confirms, false otherwise.
   */
  private static boolean promptUserConfirmation() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println(
          "Are you sure you want to execute the command with elevated privileges? (yes/no)");
      String response = scanner.nextLine().trim().toLowerCase();
      if (response.equals("yes")) {
        logger.info("User confirmed privilege escalation.");
        return true;
      } else if (response.equals("no")) {
        logger.info("User denied privilege escalation.");
        return false;
      } else {
        System.out.println("Invalid response. Please enter 'yes' or 'no'.");
      }
    }
  }

  /**
   * Verifies if the current user has administrative or root privileges.
   *
   * @return true if the user has the necessary privileges, false otherwise.
   * @throws IOException If an I/O error occurs during the privilege check.
   */
  private static boolean checkCurrentPrivileges() throws IOException {
    String osName = System.getProperty("os.name").toLowerCase();
    String command = osName.contains("win") ? "whoami /groups" : "id -u";
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (osName.contains("win") && line.contains("Administrator")) {
          return true;
        } else if (osName.contains("nix") && line.trim().equals("0")) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Logs and attempts to recover from common issues that may occur during command execution.
   *
   * @param e The exception that occurred.
   */
  private static void handleExecutionError(Exception e) {
    logger.severe("Execution error: " + e.getMessage());
    System.out.println(
        "An error occurred: "
            + e.getMessage()
            + ". Please check your configuration and try again.");
  }

  /** Provides a detailed system report including current user privileges and environment. */
  private static void generateSystemReport() {
    String osName = System.getProperty("os.name");
    String userName = System.getProperty("user.name");
    System.out.println("System Report:");
    System.out.println("Operating System: " + osName);
    System.out.println("User Name: " + userName);
    try {
      String command = osName.toLowerCase().contains("win") ? "systeminfo" : "uname -a";
      runCommand(command);
    } catch (IOException e) {
      logger.severe("Error generating system report: " + e.getMessage());
    }
  }

  /**
   * Validates the command to ensure it does not contain dangerous or harmful instructions.
   *
   * @param command The command to validate.
   * @return true if the command is safe, false otherwise.
   */
  private static boolean validateCommand(String command) {
    String[] dangerousKeywords = {"rm -rf", "format", "shutdown"};
    for (String keyword : dangerousKeywords) {
      if (command.contains(keyword)) {
        logger.warning("Command contains dangerous keyword: " + keyword);
        return false;
      }
    }
    return true;
  }

  /**
   * Backup a file before modifying it.
   *
   * @param filePath The path of the file to back up.
   * @throws IOException If an error occurs during backup.
   */
  private static void backupFile(String filePath) throws IOException {
    String backupFilePath = filePath + ".bak";
    Files.copy(Paths.get(filePath), Paths.get(backupFilePath));
    logger.info("Backup created at: " + backupFilePath);
  }

  /**
   * Main method to initialize and trigger privilege escalation.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {
    try {
      // Verify if the current user has sufficient privileges
      if (!checkCurrentPrivileges()) {
        if (promptUserConfirmation()) {
          // Backup important configuration file before proceeding
          backupFile(CONFIG_FILE);
          gainAdminAndRootPrivileges();
        } else {
          System.out.println("Privilege escalation aborted.");
        }
      } else {
        logger.info("User already has the necessary privileges.");
        gainAdminAndRootPrivileges();
      }
    } catch (IOException | UnsupportedOperationException e) {
      handleExecutionError(e);
    }
  }
}
