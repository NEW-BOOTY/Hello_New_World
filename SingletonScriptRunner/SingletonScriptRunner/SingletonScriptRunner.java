/** 
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */

import java.io.IOException;

public class SingletonScriptRunner {
    // The single instance of the class
    private static SingletonScriptRunner instance;

    // Private constructor prevents direct instantiation from other classes
    private SingletonScriptRunner() {
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
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            Process process = processBuilder.start();
            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Script executed with exit code: " + exitCode);
        } catch (IOException e) {
            System.out.println("An error occurred while trying to run the script: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("The script execution was interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
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