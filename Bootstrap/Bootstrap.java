/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;

public class Bootstrap {
  public static void main(String[] args) {
    try {
      System.out.println("Initializing Java bootstrap...");

      // Log Start of the bootstrap process
      log("Bootstrap started.");

      // Example: Validate the environment setup
      validateEnvironment();

      // Example: Execute an external script
      executeExternalScript("./src/bootstrap/bootstrap.sh");

      // Indicating successful completion
      System.out.println("Java bootstrap completed successfully.");
    } catch (Exception e) {
      // Catch and display any errors that occur during the bootstrap process
      System.err.println("Bootstrap error: " + e.getMessage());
    }
  }

  // Method to validate the environment setup (checking if the .env file exists)
  private static void validateEnvironment() throws IOException {
    File envFile = new File(".env");
    // Throw exception if the .env file is missing
    if (!envFile.exists()) {
      throw new IOException(".env file is missing.");
    }
    // If the file exists, print a confirmation message
    System.out.println(".env file verified.");
  }

  // Method to execute an external shell script using ProcessBuilder
  private static void executeExternalScript(String scriptPath)
      throws IOException, InterruptedException {
    // Create a ProcessBuilder to run the script using bash
    ProcessBuilder pb = new ProcessBuilder("bash", scriptPath);
    // Start the process
    Process process = pb.start();
    // Wait for the script to complete
    process.waitFor();
    // Check if the script executed successfully, if not, throw an exception
    if (process.exitValue() != 0) {
      throw new IOException("Script execution failed.");
    }
    // Print a success message if the script executed successfully
    System.out.println("External script executed successfully.");
  }

  // Method to log messages into a file (application.log)
  private static void log(String message) {
    try (FileWriter fw = new FileWriter("logs/application.log", true)) {
      // Write the log message to the log file
      fw.write(message + "\n");
    } catch (IOException e) {
      // If logging fails, print the error message to the console
      System.err.println("Logging error: " + e.getMessage());
    }
  }
}
