/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WormSimulationEnhanced {

  // Class representing a device in the network
  static class Device {
    private String deviceName;
    private boolean isInfected;
    private boolean isVulnerable; // Indicates if the device has a security flaw

    public Device(String deviceName, boolean isVulnerable) {
      this.deviceName = deviceName;
      this.isInfected = false;
      this.isVulnerable = isVulnerable;
    }

    public String getDeviceName() {
      return deviceName;
    }

    public boolean isInfected() {
      return isInfected;
    }

    public void infect() {
      this.isInfected = true;
    }

    public boolean isVulnerable() {
      return isVulnerable;
    }
  }

  public static void main(String[] args) {
    System.out.println("=== Ethical Worm Simulation with Enhanced Activities ===\n");

    // Create a simulated network of devices with some vulnerabilities
    List<Device> network = createNetwork();

    // Infect an initial vulnerable device
    Device initialDevice = network.get(0);
    initialDevice.infect();
    System.out.println("Worm initiated on: " + initialDevice.getDeviceName());

    // Simulate worm spreading and vulnerability detection
    simulateWormActivity(network);
  }

  // Create a simulated network with random vulnerabilities
  private static List<Device> createNetwork() {
    Random random = new Random();
    List<Device> network = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      boolean isVulnerable = random.nextBoolean(); // Randomly assign vulnerability
      network.add(new Device("Device-" + i, isVulnerable));
    }

    System.out.println("Network created with the following devices:\n");
    for (Device device : network) {
      String status = device.isVulnerable() ? "Vulnerable" : "Secure";
      System.out.println("- " + device.getDeviceName() + " [" + status + "]");
    }
    System.out.println();
    return network;
  }

  // Simulate worm scanning, spreading, and activity logging
  private static void simulateWormActivity(List<Device> network) {
    Random random = new Random();
    boolean newInfection = true;

    while (newInfection) {
      newInfection = false;
      for (Device device : network) {
        if (device.isInfected()) {
          System.out.println("\n" + device.getDeviceName() + " is scanning the network...");

          for (Device target : network) {
            if (!target.isInfected() && target.isVulnerable() && random.nextBoolean()) {
              target.infect();
              logActivity(target, "INFECTED: Device exploited due to vulnerability.");
              newInfection = true;
            }
          }
        }
      }

      if (!newInfection) {
        System.out.println("\nNo further infections detected. The worm activity has ceased.\n");
      }
    }

    displayFinalNetworkStatus(network);
  }

  // Log activity of the worm (for educational purposes)
  private static void logActivity(Device device, String message) {
    System.out.println("[LOG] " + device.getDeviceName() + " - " + message);
  }

  // Display the status of the network after worm simulation
  private static void displayFinalNetworkStatus(List<Device> network) {
    System.out.println("=== Final Network Status ===\n");
    for (Device device : network) {
      String status = device.isInfected() ? "INFECTED" : "SAFE";
      String vulnerability = device.isVulnerable() ? "Vulnerable" : "Secure";
      System.out.println(
          "- " + device.getDeviceName() + " | Status: " + status + " | " + vulnerability);
    }
  }
}

/**
 * Enhanced Features in the Program
 *
 * <p>Vulnerability Identification: - Devices have a isVulnerable attribute to simulate real-world
 * security flaws. - Worm exploits vulnerabilities to spread, emphasizing the importance of securing
 * systems.
 *
 * <p>Activity Logging: - Every infection attempt and success is logged for tracking, which aligns
 * with ethical penetration testing practices. - Example log: "INFECTED: Device exploited due to
 * vulnerability."
 *
 * <p>Randomized Network Security: - Devices in the simulated network have randomized security
 * levels (vulnerable or secure). - Demonstrates how unsecured systems can compromise entire
 * networks.
 *
 * <p>Educational Focus: - Illustrates the propagation of worms, emphasizing the need for patching
 * vulnerabilities. - Does not interact with real systems, ensuring ethical usage.
 *
 * <p>Safe Network Visualization: - Final network status highlights infected, vulnerable, and secure
 * devices, teaching about infection containment and the impact of vulnerabilities.
 *
 * <p>How It Works: Initialization: - A simulated network of 10 devices is created. - Each device is
 * randomly assigned as "Vulnerable" or "Secure."
 *
 * <p>Worm Infection Process: - Starts with one infected device. - Worm scans the network and
 * infects vulnerable devices using random probabilities.
 *
 * <p>End of Simulation: - Once no further infections can occur, the simulation stops. - The program
 * outputs the final status of all devices.
 *
 * <p>Real-World Applications: - Network Security Training: Demonstrates the risks of unpatched
 * systems and vulnerable configurations. - Awareness Campaigns: Highlights how worms spread and the
 * importance of strong cybersecurity practices. - Penetration Testing Basics: Introduces the
 * concept of testing network defenses in a controlled environment.
 */
