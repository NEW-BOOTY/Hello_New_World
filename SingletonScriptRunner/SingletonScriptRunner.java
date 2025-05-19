/** 
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonScriptRunner {
    // Logger for the class
    private static final Logger logger = Logger.getLogger(SingletonScriptRunner.class.getName());

    // The single instance of the class
    private static SingletonScriptRunner instance;

    // Private constructor prevents direct instantiation from other classes
    private SingletonScriptRunner() {
        // Configure the logger if needed (e.g., file logging, formatting)
        logger.setLevel(Level.ALL);
    }

    // Method to get the single instance of the class
    public static SingletonScriptRunner getInstance() {
        if (instance == null) {
            instance = new SingletonScriptRunner();
        }
        return instance;
    }

    // Method to run a script with the provided path
    public void runScript(String scriptPath) {
        File scriptFile = new File(scriptPath);

        // Validate script path
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            logger.severe("Invalid script path: " + scriptPath);
            return;
        }

        if (!scriptFile.canExecute()) {
            logger.severe("Script does not have execute permissions: " + scriptPath);
            return;
        }

        try {
            // Execute the script
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Script executed successfully with exit code: " + exitCode);
            } else {
                logger.warning("Script executed with a non-zero exit code: " + exitCode);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while trying to run the script.", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "The script execution was interrupted.", e);
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred while running the script.", e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("Please provide the script path as an argument.");
            return;
        }

        String scriptPath = args[0];

        // Get the Singleton instance and run the script
        SingletonScriptRunner runner = SingletonScriptRunner.getInstance();
        runner.runScript(scriptPath);
    }
}
