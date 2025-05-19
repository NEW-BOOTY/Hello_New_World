/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 * Production-ready program with continuous execution, task scheduling, error handling,
 * performance monitoring, and graceful shutdown.
 */

import java.util.concurrent.*;
import java.util.logging.*;
import java.util.Random;

public class ContinuousTaskManager {

    // Logger to monitor system status and errors
    private static final Logger logger = Logger.getLogger(ContinuousTaskManager.class.getName());

    // Flag to manage graceful shutdown
    private static volatile boolean isRunning = true;

    // Random generator for simulating tasks
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Continuous Task Manager Initialized ===");
        logger.info("Program started. Running continuously...");

        // Initialize performance monitor
        PerformanceMonitor monitor = new PerformanceMonitor();

        // ThreadPool to handle scheduled tasks
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Shutdown hook for graceful exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[System] Shutdown signal received. Terminating...");
            logger.info("Shutdown signal received.");
            isRunning = false;
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("[System] Forcefully shutting down tasks...");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.severe("Error during shutdown: " + e.getMessage());
            }
            System.out.println("[System] Tasks terminated. Goodbye!");
            logger.info("Program terminated gracefully.");
        }));

        // Schedule a task to continuously run every 2 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) {
                try {
                    long startTime = System.currentTimeMillis();
                    performTask();
                    long timeTaken = System.currentTimeMillis() - startTime;
                    monitor.logPerformance("Task Execution", timeTaken);
                } catch (Exception e) {
                    logger.severe("Error executing task: " + e.getMessage());
                }
            }
        }, 0, 2, TimeUnit.SECONDS);

        // Keep the main thread alive to manage program lifecycle
        while (isRunning) {
            try {
                Thread.sleep(1000); // Monitor program every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.severe("Main thread interrupted: " + e.getMessage());
            }
        }
    }

    /**
     * Simulated task for continuous execution.
     * It performs simple random operations and logs outputs.
     */
    private static void performTask() {
        System.out.println("[TaskManager] Performing scheduled task...");
        int taskResult = random.nextInt(1000); // Simulate a task computation
        if (taskResult % 7 == 0) { // Simulate an occasional error
            throw new RuntimeException("Simulated task failure with result: " + taskResult);
        }
        System.out.println("[TaskManager] Task completed successfully. Result: " + taskResult);
        logger.info("Task executed successfully. Result: " + taskResult);
    }

    /**
     * Monitors and logs system performance metrics.
     */
    static class PerformanceMonitor {
        private final ConcurrentHashMap<String, Long> performanceLog = new ConcurrentHashMap<>();

        /**
         * Logs the time taken for a specific task.
         *
         * @param taskName  Name of the task.
         * @param timeTaken Time taken to complete the task.
         */
        public void logPerformance(String taskName, long timeTaken) {
            performanceLog.put(taskName, timeTaken);
            System.out.println("[PerformanceMonitor] " + taskName + " completed in " + timeTaken + " ms.");
            logger.info(taskName + " execution time: " + timeTaken + " ms.");
        }
    }
}
