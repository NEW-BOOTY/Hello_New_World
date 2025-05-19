/** 
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SingletonScriptRunner {
    // Logger instance
    private static final Logger logger = LoggerFactory.getLogger(SingletonScriptRunner.class);

    // The single instance of the class (volatile for thread-safety)
    private static volatile SingletonScriptRunner instance;

    // Private constructor prevents direct instantiation
    private SingletonScriptRunner() {
    }

    // Method to get the single instance of the class (Thread-safe Singleton)
    public static SingletonScriptRunner getInstance() {
        if (instance == null) {
            synchronized (SingletonScriptRunner.class) {
                if (instance == null) {
                    instance = new SingletonScriptRunner();
                }
            }
        }
        return instance;
    }

    // Method to run a script with the provided path
    public void runScript(String scriptPath) {
        Process process = null;
        try {
            logger.info("Attempting to execute script: {}", scriptPath);
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            process = processBuilder.start();

            // Capture and log the script's standard output
            try (BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String line;
                logger.info("Standard Output:");
                while ((line = stdOut.readLine()) != null) {
                    logger.info(line);
                }

                logger.error("Error Output (if any):");
                while ((line = stdErr.readLine()) != null) {
                    logger.error(line);
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            logger.info("Script executed with exit code: {}", exitCode);
        } catch (IOException e) {
            logger.error("An error occurred while trying to run the script: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("The script execution was interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy();
                logger.info("Process resources cleaned up.");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the script path as an argument.");
            return;
        }

        // Get the Singleton instance and run the script
        SingletonScriptRunner runner = SingletonScriptRunner.getInstance();
        runner.runScript(args[0]);
    }
}
