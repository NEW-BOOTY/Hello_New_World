/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */
import java.io.*;

public class MimicAndReplicate {
  public static void main(String[] args) {
    // Command to be executed
    String command = "yourCommandHere"; // Replace with the actual command to execute

    try {
      // Execute the command
      Process process = executeCommand(command);

      if (process != null) {
        // Handle the process output
        handleProcessOutput(process);

        // Wait for the process to complete and check its exit value
        int exitCode = process.waitFor();
        System.out.println("Command executed with exit code: " + exitCode);
      }
    } catch (IOException e) {
      System.err.println("Error during command execution: " + e.getMessage());
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.err.println("Command execution interrupted: " + e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Executes the specified command and returns the Process object.
   *
   * @param command The command to execute
   * @return The Process object representing the running command
   * @throws IOException If an I/O error occurs
   */
  private static Process executeCommand(String command) throws IOException {
    // Initialize the process builder with the specified command
    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    processBuilder.redirectErrorStream(true); // Merge standard error with standard output
    return processBuilder.start();
  }

  /**
   * Handles the output from the specified process.
   *
   * @param process The process whose output is to be handled
   * @throws IOException If an I/O error occurs
   */
  private static void handleProcessOutput(Process process) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }
}
