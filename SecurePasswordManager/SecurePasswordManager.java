/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class SecurePasswordManager {

    // Generate a random salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash a password with SHA-256 and salt
    public static String hashPasswordSHA256(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    // Hash a password using bcrypt
    public static String hashPasswordBcrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Verify a password using bcrypt
    public static boolean verifyPasswordBcrypt(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            // SHA-256 Hashing with Salt
            String salt = generateSalt();
            String hashedSHA256 = hashPasswordSHA256(password, salt);
            System.out.println("SHA-256 Hashed Password with Salt: " + hashedSHA256);

            // Bcrypt Hashing
            String hashedBcrypt = hashPasswordBcrypt(password);
            System.out.println("Bcrypt Hashed Password: " + hashedBcrypt);

            // Verification for Bcrypt
            System.out.print("Verify password: ");
            String verifyPassword = scanner.nextLine();
            boolean isMatch = verifyPasswordBcrypt(verifyPassword, hashedBcrypt);

            if (isMatch) {
                System.out.println("Password verified successfully!");
            } else {
                System.out.println("Password verification failed!");
            }

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
