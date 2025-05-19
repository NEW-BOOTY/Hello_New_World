/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.IOException;
import java.util.logging.Logger;

public class AdminPrivilegesManager {

    private static final Logger logger = Logger.getLogger(AdminPrivilegesManager.class.getName());

    public static void main(String[] args) {
        logger.info("Starting Admin Privileges Manager...");

        // Call privilege escalation methods as needed
        gainAdminPrivilegesWindows();
        gainAdminPrivilegesLinux();
        removeFirmwarePasswordsMac();
    }

    // Method to disable UAC on Windows
    private static void gainAdminPrivilegesWindows() {
        String command = "REG ADD HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System /v EnableLUA /t REG_DWORD /d 0 /f";
        executeCommand("cmd.exe", "/c", command, "UAC disabled on Windows.");
    }

    // Method to use sudo for root privileges on Linux
    private static void gainAdminPrivilegesLinux() {
        String command = "sudo -i";
        executeCommand("bash", "-c", command, "Root privileges granted on Linux.");
    }

    // Method to remove firmware passwords on macOS
    private static void removeFirmwarePasswordsMac() {
        String script = "nvram -c";
        executeCommand("osascript", "-e", script, "Firmware passwords removed on macOS.");
    }

    // Generic method to execute system commands
    private static void executeCommand(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            p.waitFor();
            logger.info(command[command.length - 1]);
        } catch (IOException | InterruptedException e) {
            logger.severe("Error executing command: " + e.getMessage());
        }
    }
}
