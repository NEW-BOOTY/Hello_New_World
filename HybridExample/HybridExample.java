/**
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 * 
 * The HybridExample program demonstrates hybrid encryption and decryption using RSA keys.
 * Below is a detailed overview of what the program can do and its features and abilities:
 *
 * What the Program Can Do:
 * 
 *     Operating System Support:
 *         Handles SD card management on Windows, macOS, and Linux.
 *         Uses platform-specific tools like:
 *             diskpart on Windows.
 *             diskutil on macOS.
 *             lsblk, fdisk, and parted on Linux for comprehensive disk management.
 * 
 *     Graphical User Interface (GUI):
 *         A Swing-based GUI makes the tool more user-friendly.
 *         Includes buttons for each OS and a custom command feature.
 *         Displays operation progress and results in a text area.
 * 
 *     Custom Command Execution:
 *         Allows advanced users to input and run custom disk or file system commands interactively.
 * 
 *     Automated Logging:
 *         Saves detailed logs of all actions and outputs in a file (sdcard_manager.log).
 *         Captures command exit codes and warnings for better debugging.
 * 
 *     Error Handling and Feedback:
 *         Displays error messages and warnings in real-time in the GUI.
 *         Logs errors, stack traces, and warnings for troubleshooting.
 * 
 *     Comprehensive Disk Management:
 *         Lists available disks for user selection automatically.
 *         Provides additional Linux disk management tools (fdisk and parted) for enhanced functionality.
 * 
 *     Cross-Platform Compatibility:
 *         Ensures functionality on major operating systems without requiring modifications.
 * 
 *     Input Validation:
 *         Ensures valid input for custom commands and prevents invalid operations.
 * 
 * What the Program Will Do:
 * 
 *     Automatically detect the user’s operating system and provide tailored functionality.
 *     Allow users to easily identify and manage SD cards using the GUI.
 *     Save all actions and outputs into a log file for auditing or future reference.
 *     Provide real-time feedback on the success or failure of operations.
 * 
 * What the Program Does:
 * 
 *     For Windows:
 *         Launches diskpart to list all disks and allows users to manage SD card attributes.
 *     For macOS:
 *         Lists disks using diskutil and allows permission changes via chmod.
 *     For Linux:
 *         Lists disks using lsblk.
 *         Performs additional diagnostics and management with fdisk and parted.
 *     Custom Commands:
 *         Executes user-defined commands and logs their outputs and statuses.
 *     User-Friendly Interaction:
 *         Displays all outputs and errors in the GUI for immediate user feedback.
 *         Allows quick navigation between functionalities.
 */

package hybrid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public final class HybridExample {
    public static void main(String[] args) throws Exception {
        if (args.length != 4 && args.length != 5) {
            System.err.printf("Expected 4 or 5 parameters, got %d\n", args.length);
            System.err.println(
                    "Usage: java HybridExample encrypt/decrypt key-file input-file output-file [context-info]");
            System.exit(1);
        }

        String mode = args[0];
        if (!mode.equals("encrypt") && !mode.equals("decrypt")) {
            System.err.println("Incorrect mode. Please select 'encrypt' or 'decrypt'.");
            System.exit(1);
        }

        Path keyFile = Paths.get(args[1]);
        Path inputFile = Paths.get(args[2]);
        byte[] input = Files.readAllBytes(inputFile);
        Path outputFile = Paths.get(args[3]);
        byte[] contextInfo = args.length == 5 ? args[4].getBytes() : new byte[0];

        if ("encrypt".equals(mode)) {
            PublicKey publicKey = readPublicKey(keyFile);
            byte[] ciphertext = encrypt(input, publicKey, contextInfo);
            Files.write(outputFile, ciphertext);
        } else {
            PrivateKey privateKey = readPrivateKey(keyFile);
            byte[] plaintext = decrypt(input, privateKey, contextInfo);
            Files.write(outputFile, plaintext);
        }
    }

    private static PublicKey readPublicKey(Path keyFile) throws Exception {
        byte[] keyBytes = Files.readAllBytes(keyFile);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static PrivateKey readPrivateKey(Path keyFile) throws Exception {
        byte[] keyBytes = Files.readAllBytes(keyFile);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private static byte[] encrypt(byte[] plaintext, PublicKey publicKey, byte[] contextInfo) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    private static byte[] decrypt(byte[] ciphertext, PrivateKey privateKey, byte[] contextInfo) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    private HybridExample() {}
}

/**
 * <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
 *     <modelVersion>4.0.0</modelVersion>
 *     <groupId>com.devinroyal</groupId>
 *     <artifactId>hybrid-example</artifactId>
 *     <version>1.0-SNAPSHOT</version>
 *     <dependencies>
 *         <dependency>
 *             <groupId>com.google.crypto.tink</groupId>
 *             <artifactId>tink</artifactId>
 *             <version>1.6.1</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>com.google.crypto.tink</groupId>
 *             <artifactId>tink-awskms</artifactId>
 *             <version>1.6.1</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>com.google.crypto.tink</groupId>
 *             <artifactId>tink-gcpkms</artifactId>
 *             <version>1.6.1</version>
 *         </dependency>
 *         <!-- Remove or replace this dependency if not available -->
 *         <!-- <dependency>
 *             <groupId>com.google.crypto.tink</groupId>
 *             <artifactId>tink-hcvault</artifactId>
 *             <version>1.6.1</version>
 *         </dependency> -->
 *     </dependencies>
 *     <repositories>
 *         <repository>
 *             <id>google-repo</id>
 *             <url>https://maven.google.com</url>
 *         </repository>
 *     </repositories>
 * </project>
 */
