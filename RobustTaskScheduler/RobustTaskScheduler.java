/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */
import java.util.*;
import java.util.concurrent.*;

/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 *
 * <p>This class simulates a robust task scheduler system. It handles scheduling, executing tasks,
 * cancellation, shutdowns with failure prevention, and recovery mechanisms.
 */
public class RobustTaskScheduler {

  private final SortedMap<Long, ScheduledTask> taskMap = new TreeMap<>();
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private volatile boolean isShuttingDown = false;

  // Metrics and monitoring
  private long totalScheduledTasks = 0;
  private long totalExecutedTasks = 0;
  private long totalExecutionFailures = 0;

  public static void main(String[] args) {
    RobustTaskScheduler scheduler = new RobustTaskScheduler();

    // Schedule tasks
    try {
      scheduler.scheduleTask(() -> System.out.println("Task 1 executed"), 5);
      scheduler.scheduleTask(() -> System.out.println("Task 2 executed"), 10);
      scheduler.scheduleTask(() -> System.out.println("Task 3 executed"), 2);

      // Wait for all tasks to execute
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      System.err.println("Main thread interrupted: " + e.getMessage());
      Thread.currentThread().interrupt();
    } catch (IllegalArgumentException e) {
      System.err.println("Error scheduling task: " + e.getMessage());
    }

    // Gracefully shutdown the scheduler
    scheduler.shutdown();
  }

  /**
   * Schedules a task to be executed after a specified delay.
   *
   * @param task The task to be executed.
   * @param delaySeconds The delay in seconds before the task is executed.
   * @throws IllegalArgumentException If delaySeconds is negative or zero.
   */
  public void scheduleTask(Runnable task, int delaySeconds) {
    if (task == null) {
      System.err.println("Task cannot be null.");
      return;
    }
    if (delaySeconds <= 0) {
      System.err.println("Delay must be positive and greater than zero.");
      return;
    }

    long scheduledTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delaySeconds);
    taskMap.put(scheduledTime, new ScheduledTask(task, scheduledTime));

    try {
      // Schedule the task with a delay
      executor.schedule(() -> executeTask(scheduledTime), delaySeconds, TimeUnit.SECONDS);
      totalScheduledTasks++;
    } catch (RejectedExecutionException e) {
      System.err.println("Error scheduling task: " + e.getMessage());
      taskMap.remove(scheduledTime); // Remove the task if scheduling fails
    }
  }

  /**
   * Executes the task scheduled at the specified time.
   *
   * @param scheduledTime The time at which the task was scheduled to be executed.
   */
  private void executeTask(long scheduledTime) {
    ScheduledTask scheduledTask = taskMap.remove(scheduledTime);
    if (scheduledTask != null) {
      try {
        System.out.println("Executing task at " + new Date());
        scheduledTask.getTask().run();
        totalExecutedTasks++;
      } catch (Exception e) {
        System.err.println("Task execution failed: " + e.getMessage());
        totalExecutionFailures++;
      }
    } else {
      System.err.println("Task not found for scheduled time: " + new Date(scheduledTime));
    }
  }

  /**
   * Cancels a scheduled task based on its scheduled time.
   *
   * @param scheduledTime The time at which the task was scheduled to be executed.
   * @return true if the task was cancelled successfully, false otherwise.
   */
  public boolean cancelTask(long scheduledTime) {
    if (taskMap.containsKey(scheduledTime)) {
      taskMap.remove(scheduledTime);
      System.out.println("Task scheduled for " + new Date(scheduledTime) + " has been cancelled.");
      return true;
    } else {
      System.err.println(
          "No task found for cancellation at scheduled time: " + new Date(scheduledTime));
      return false;
    }
  }

  /** Shuts down the scheduler and waits for tasks to complete. */
  public void shutdown() {
    if (isShuttingDown) {
      System.err.println("Scheduler is already shutting down.");
      return;
    }

    isShuttingDown = true;
    executor.shutdown();
    try {
      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        System.err.println(
            "Scheduler did not terminate in the allotted time. Attempting forced shutdown...");
        executor.shutdownNow();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
          System.err.println("Scheduler did not terminate properly.");
        }
      }
    } catch (InterruptedException ex) {
      System.err.println("Shutdown interrupted: " + ex.getMessage());
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    } finally {
      System.out.println("Scheduler shutdown complete.");
      displayMetrics();
    }
  }

  /** Displays metrics regarding task scheduling and execution. */
  private void displayMetrics() {
    System.out.println("Scheduler Metrics:");
    System.out.println("Total Scheduled Tasks: " + totalScheduledTasks);
    System.out.println("Total Executed Tasks: " + totalExecutedTasks);
    System.out.println("Total Task Execution Failures: " + totalExecutionFailures);
  }

  /** A wrapper class for Scheduled Task to include its execution time and task details. */
  private static class ScheduledTask {
    private final Runnable task;
    private final long scheduledTime;

    public ScheduledTask(Runnable task, long scheduledTime) {
      this.task = task;
      this.scheduledTime = scheduledTime;
    }

    public Runnable getTask() {
      return task;
    }

    public long getScheduledTime() {
      return scheduledTime;
    }
  }
}
