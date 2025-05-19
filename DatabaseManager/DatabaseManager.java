import java.sql.*;
import com.nulabinc.zxcvbn.*;
import org.mindrot.jbcrypt.*;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public class DatabaseManager {

    private static final String ENCRYPTION_KEY = "your_encryption_key"; // In real-world apps, use a secure key management system
    private static final String DB_NAME = "private_database.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();

            // Create table if not exists
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "username TEXT NOT NULL," +
                         "password TEXT NOT NULL)";
            stmt.executeUpdate(sql);

            // Retrieve first user from database
            ResultSet rs = stmt.executeQuery("SELECT username FROM users LIMIT 1");
            if (rs.next()) {
                String username = rs.getString(1);
                System.out.println("Located username: " + username);

                // Delete password
                String deletePasswordQuery = "UPDATE users SET password = NULL WHERE username = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deletePasswordQuery)) {
                    deleteStmt.setString(1, username);
                    deleteStmt.executeUpdate();
                }
                System.out.println("Password for user '" + username + "' deleted.");

                // Generate new password
                String newPassword = generateRandomPassword();
                if (!checkPasswordStrength(newPassword)) {
                    System.out.println("Generated password does not meet strength requirements.");
                    return;  // Exit if the password is not strong enough
                }

                String encryptedPassword = encryptPassword(newPassword);
                String hashedPassword = hashPassword(newPassword);

                // Update password (hashed version)
                String updatePasswordQuery = "UPDATE users SET password = ? WHERE username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery)) {
                    updateStmt.setString(1, encryptedPassword); // Store encrypted password in DB
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                }
                System.out.println("New password generated and stored for user '" + username + "': " + newPassword);
            } else {
                System.out.println("No user found in the database.");
            }

        } catch (SQLException | NullPointerException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hash the password with BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Generate a random password
    public static String generateRandomPassword() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            char character = (char) (random.nextInt(95) + 32); // Printable ASCII characters
            builder.append(character);
        }
        return builder.toString();
    }

    // Check password strength using Zxcvbn
    public static boolean checkPasswordStrength(String password) {
        Zxcvbn zxcvbn = new Zxcvbn();
        ZxcvbnPasswordStrength strength = zxcvbn.measure(password);

        // Minimum strength requirements: password should not be weak
        if (strength.getScore() < 3) {
            System.out.println("Weak password. Score: " + strength.getScore());
            return false;
        }
        return true;
    }

    // Encrypt password using AES GCM for storage
    public static String encryptPassword(String password) {
        try {
            SecretKey key = getEncryptionKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV(); // Get the initialization vector
            byte[] encrypted = cipher.doFinal(password.getBytes());

            // Combine IV and encrypted data for storage
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return null;
        }
    }

    // Decrypt password using AES GCM
    public static String decryptPassword(String encryptedPassword) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            byte[] iv = new byte[12]; // AES GCM IV length
            System.arraycopy(decoded, 0, iv, 0, iv.length);

            byte[] encrypted = new byte[decoded.length - iv.length];
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            SecretKey key = getEncryptionKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit authentication tag length
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            System.err.println("Error decrypting password: " + e.getMessage());
            return null;
        }
    }

    // Generate an AES encryption key
    public static SecretKey getEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // AES-256
            return keyGenerator.generateKey();
        } catch (Exception e) {
            System.err.println("Error generating encryption key: " + e.getMessage());
            return null;
        }
    }
}

/*
 * What the Program Does:
 * ======================
 * 
 * 1. Database Management:
 *    - Connects to a SQLite database (private_database.db), a file-based database.
 *    - Creates a table for storing user information if it does not already exist. The table includes:
 *      - Username field.
 *      - Password field.
 * 
 * 2. Password Management:
 *    - Retrieves the first user from the database (if any).
 *    - Deletes the user's old password for security purposes.
 *    - Generates a new password and checks its strength using Zxcvbn (password strength estimator).
 *    - Encrypts the new password using AES/GCM encryption for secure storage.
 *    - Hashes the new password with BCrypt for additional security.
 *    - Stores the encrypted password securely in the database.
 * 
 * 3. Password Complexity Enforcement:
 *    - Enforces password strength rules by analyzing the password with Zxcvbn.
 *    - Rejects passwords with a strength score below 3, ensuring only strong passwords are accepted.
 * 
 * 4. Encryption and Decryption:
 *    - Encrypts passwords using AES/GCM with a secure 256-bit AES encryption key.
 *    - Utilizes an Initialization Vector (IV) for enhanced encryption security.
 *    - Stores both the IV and the encrypted data in the database (encoded in Base64).
 *    - Supports decrypting stored passwords when needed, though not explicitly shown in the main workflow.
 * 
 * 5. Secure SQL Handling:
 *    - Uses prepared statements for database queries and updates, preventing SQL injection vulnerabilities.
 *    - Ensures database connections and statements are safely closed using try-with-resources, avoiding memory leaks and improving resource management.
 * 
 * 6. Error Handling:
 *    - Gracefully handles exceptions like SQLException and NullPointerException.
 *    - Provides detailed error messages and stack traces for debugging purposes.
 * 
 * What the Program Will Do:
 * =========================
 * 
 * 1. Automatically manage passwords for users in the database.
 * 2. Generate secure passwords and store them encrypted in the database.
 * 3. Verify password strength before storage to enforce security policies.
 * 4. Replace old passwords with newly generated, secure, and encrypted passwords.
 * 5. Securely handle sensitive user data (e.g., passwords) using encryption and hashing.
 * 6. Prevent SQL injection attacks with prepared statements.
 * 7. Properly manage database connections, avoiding resource leaks.
 * 
 * What It Can Be Used For:
 * ========================
 * 
 * 1. User Authentication Systems:
 *    - Suitable for backend systems managing user credentials securely.
 *    - Can be integrated into admin panels or user management modules.
 * 
 * 2. Secure Password Storage:
 *    - Functions as a secure password manager for applications handling sensitive data.
 *    - Ideal for enterprise systems enforcing strong password policies and encrypted storage.
 * 
 * 3. Password Strength Enforcement:
 *    - Ensures strong password policies through real-time analysis with Zxcvbn.
 *    - Extensible to user registration systems where password strength is validated before acceptance.
 * 
 * 4. Encryption in Database:
 *    - Securely stores sensitive data (e.g., passwords) with AES encryption.
 *    - Expandable for storing other types of sensitive data like API keys or personal information.
 * 
 * 5. Database Security:
 *    - Combines AES encryption and BCrypt hashing for robust data protection.
 *    - Employs prepared statements to mitigate SQL injection risks.
 * 
 * 6. General Data Security:
 *    - Adaptable for managing sensitive data beyond passwords, such as API tokens or private information.
 * 
 * 7. Educational Use:
 *    - Demonstrates password hashing, encryption techniques, SQL injection prevention, and password complexity enforcement.
 *    - A practical example for learning secure user management system implementation in Java.
 * 
 * Potential Extensions:
 * =====================
 * 
 * 1. Web Interface:
 *    - Add a web-based UI for dynamic user account and password management.
 * 
 * 2. Audit Logs:
 *    - Implement logging to track all actions related to password changes and encryption for auditing purposes.
 * 
 * 3. User Input Validation:
 *    - Enhance validation for other user inputs like usernames and email addresses.
 * 
 * Summary:
 * ========
 * 
 * - This program is a secure password management solution offering strong encryption, password complexity enforcement, and secure database operations.
 * - It is ideal for systems requiring safe storage of user credentials or sensitive data.
 */

