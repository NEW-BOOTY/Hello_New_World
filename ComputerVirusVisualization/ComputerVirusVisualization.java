/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerVirusVisualization {
  public static void main(String[] args) {
    System.out.println("=== Understanding a Computer Virus ===");
    displayVirusDefinition();
    displayVirusCharacteristics();
    simulateVirusSpread();
  }

  private static void displayVirusDefinition() {
    System.out.println("\n--- Definition ---");
    System.out.println("A computer virus is a malicious piece of code that can replicate itself ");
    System.out.println("and spread to other computers by attaching to existing files or programs.");
  }

  private static void displayVirusCharacteristics() {
    System.out.println("\n--- Key Characteristics ---");
    System.out.println("1. Self-replication: Copies itself and infects other files.");
    System.out.println(
        "2. Harmful actions: Deletes data, steals sensitive information, disrupts systems.");
    System.out.println(
        "3. Transmission: Spreads through infected emails, downloads, USB drives, etc.");
    System.out.println(
        "4. No hardware damage: Affects software only, not physical components like CPU or RAM.");
  }

  private static void simulateVirusSpread() {
    System.out.println("\n--- Visualizing Virus Spread ---");
    String[] files = {
      "Document1.docx", "Photo.jpg", "Video.mp4", "Spreadsheet.xlsx", "Presentation.pptx"
    };
    String[] infectionMethods = {"Email", "Download", "USB Drive", "Website"};
    List<String> infectedFiles = new ArrayList<>();
    Random random = new Random();

    System.out.println("\nInitial System State:");
    for (String file : files) {
      System.out.println("[ ] " + file);
    }

    System.out.println("\nVirus infecting files...");
    for (String file : files) {
      if (random.nextBoolean()) {
        infectedFiles.add(file);
      }
    }

    System.out.println("\nSystem State After Infection:");
    for (String file : files) {
      if (infectedFiles.contains(file)) {
        System.out.println("[X] " + file + " (Infected)");
      } else {
        System.out.println("[ ] " + file);
      }
    }

    System.out.println("\n--- Transmission Methods ---");
    for (String method : infectionMethods) {
      System.out.println("* Spread via: " + method);
    }

    displayAsciiVirus();
  }

  private static void displayAsciiVirus() {
    System.out.println("\n--- Virus Visual ---");
    System.out.println("         .----.        ");
    System.out.println("       .'      '.      ");
    System.out.println("      /  .--.    \\    ");
    System.out.println("     |  ( o )     |   ");
    System.out.println("      \\  '--'    /    ");
    System.out.println("       '.______.''    ");
    System.out.println("    /\\   .-----.   /\\ ");
    System.out.println("   //\\\\_/       \\_//\\\\");
    System.out.println("   \\_/     X     \\_/   ");
    System.out.println("          / \\           ");
    System.out.println("         (   )          ");
    System.out.println("          '-'            ");
    System.out.println("\nVirus \"tentacles\" attach to files, spreading infection.");
  }
}
