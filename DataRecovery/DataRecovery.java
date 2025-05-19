/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.util.logging.*;

public class DataRecovery {

  // Logger for handling logs and errors
  private static final Logger logger = Logger.getLogger(DataRecovery.class.getName());

  public static void main(String[] args) {
    // Set up logger configuration
    setupLogging();

    // Check for proper command-line arguments
    if (args.length < 2) {
      logger.severe(
          "Insufficient arguments. Usage: <data-recovery-tool-path> <path-to-formatted-drive>");
      return;
    }

    String toolPath = args[0]; // Path to the data recovery tool
    String drivePath = args[1]; // Path to the formatted drive

    // Validate paths
    if (!isValidPath(toolPath)) {
      logger.severe("Invalid tool path: " + toolPath);
      return;
    }

    if (!isValidPath(drivePath)) {
      logger.severe("Invalid drive path: " + drivePath);
      return;
    }

    // Execute the data recovery process
    executeDataRecovery(toolPath, drivePath);
  }

  /** Sets up logging configuration. */
  private static void setupLogging() {
    try {
      // Set up console handler with simple formatting
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(Level.ALL);
      SimpleFormatter formatter = new SimpleFormatter();
      consoleHandler.setFormatter(formatter);
      logger.addHandler(consoleHandler);

      // Set log level to ALL for comprehensive logging
      logger.setLevel(Level.ALL);
    } catch (SecurityException e) {
      logger.severe("Error setting up logging: " + e.getMessage());
    }
  }

  /**
   * Validates the existence of the given path.
   *
   * @param path The path to validate.
   * @return True if the path is valid, otherwise false.
   */
  private static boolean isValidPath(String path) {
    File file = new File(path);
    return file.exists() && file.isDirectory();
  }

  /**
   * Executes the data recovery tool with the given tool path and drive path.
   *
   * @param toolPath The path to the data recovery tool.
   * @param drivePath The path to the formatted drive.
   */
  private static void executeDataRecovery(String toolPath, String drivePath) {
    try {
      // Construct the process command
      ProcessBuilder builder = new ProcessBuilder(toolPath, "recover", drivePath);
      builder.redirectErrorStream(true); // Combine standard output and error streams

      // Start the process
      Process process = builder.start();

      // Read and log the output of the data recovery tool
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          logger.info(line); // Log each output line
        }
      }

      // Wait for the process to complete
      int exitCode = process.waitFor();
      logger.info("Data recovery process finished with exit code: " + exitCode);

      if (exitCode != 0) {
        logger.severe("Data recovery failed. Check the tool's output for details.");
      } else {
        logger.info("Data recovery completed successfully.");
      }
    } catch (IOException | InterruptedException e) {
      logger.severe("Error during data recovery process: " + e.getMessage());
      Thread.currentThread().interrupt(); // Restore interrupted status
    }
  }
}
