/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 * Unauthorized use, distribution, or reproduction of this code is prohibited without written consent from the author.
 * Email: PAY_ME@MY.COM; JAVA-DEVELOPER@PROGRAMMER.NET
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SDCardReadWriteManager {

    public static void main(String[] args) {
        System.out.println("Starting SD Card Read/Write Management Tool...");
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                handleWindows();
            } else if (os.contains("mac")) {
                handleMac();
            } else {
                System.err.println("Unsupported operating system.");
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleWindows() throws IOException, InterruptedException {
        System.out.println("Detected Windows OS...");
        System.out.println("Launching DiskPart to identify disks...");

        // List disks using diskpart
        executeCommand("cmd.exe", "/c", "diskpart", "/s", "list disk");
        System.out.println("Please identify the SD card number from the list above.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the disk number of your SD card: ");
        String diskNumber = reader.readLine();

        // Clear readonly attribute
        executeCommand("cmd.exe", "/c", "diskpart", "/s", "select disk " + diskNumber + " & attributes disk clear readonly");
        System.out.println("Readonly attribute cleared successfully.");
    }

    private static void handleMac() throws IOException, InterruptedException {
        System.out.println("Detected macOS...");
        System.out.println("Listing available disks using 'diskutil list'...");

        // List disks using diskutil
        executeCommand("diskutil", "list");
        System.out.println("Please identify the SD card name from the list above.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the name of your SD card (e.g., /Volumes/MySDCard): ");
        String sdCardPath = reader.readLine();

        // Modify permissions using chmod
        executeCommand("sudo", "chmod", "777", sdCardPath);
        System.out.println("Permissions successfully updated for: " + sdCardPath);

        // Advanced diskutil commands
        System.out.println("Do you want to perform advanced disk management? (yes/no): ");
        String response = reader.readLine();
        if (response.equalsIgnoreCase("yes")) {
            System.out.println("Enter a 'diskutil' command (e.g., eraseDisk, verifyVolume, etc.): ");
            String command = reader.readLine();
            executeCommand("diskutil", command.split(" "));
            System.out.println("Diskutil command executed successfully.");
        }
    }

    private static void executeCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        logProcessOutput(process);
        process.waitFor();
    }

    private static void logProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}


/** 
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 *
 * Permission is hereby granted to any individual or legal entity (hereinafter "Licensee") obtaining a copy of this software and associated documentation files (the "Software"), to use the Software for personal or internal business purposes, subject to the following conditions:
 * 
 * 1. **No Distribution**: Licensee may not distribute, sublicense, or resell the Software in any form, whether modified or unmodified, without the express written permission of the copyright holder.
 * 
 * 2. **Modifications**: Licensee may modify the Software for personal or internal business purposes but may not distribute such modifications or derivative works.
 * 
 * 3. **Commercial Use**: Any commercial use of the Software, including using the Software to provide services to third parties, requires a separate commercial license from the copyright holder. Contact the copyright holder to negotiate terms.
 * 
 * 4. **Profit Sharing**: If Licensee profits from the use of the Software, whether directly or indirectly, 50% of the profits must be shared with the copyright holder unless a different arrangement has been agreed upon in writing.
 * 
 * 5. **Attribution**: Licensee must include the original copyright notice in all copies or substantial portions of the Software.
 * 
 * 6. **No Warranty**: The Software is provided "as is," without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and non-infringement. In no event shall the copyright holder be liable for any claim, damages, or other liability, whether in an action of contract, tort, or otherwise, arising from, out of, or in connection with the Software or the use or other dealings in the Software.
 *
 * For any questions, please contact the copyright holder at PAY_ME@MY.COM; JAVA-DEVELOPER@PROGRAMMER.NET.
 */