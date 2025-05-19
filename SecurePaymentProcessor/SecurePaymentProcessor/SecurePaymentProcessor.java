/**
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 *
 * Unauthorized use, distribution, or reproduction of this code and/or software is prohibited without written consent from the author. If another entity, person, corporation, or organization profits from this creation, software, and/or code, then the profit must be split 50/50 with the author. Any further sharing must also adhere to these terms. For any questions, please contact the author.
 * Email: PAY_ME@MY.COM; JAVA-DEVELOPER@PROGRAMMER.NET
 */

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;

public class SecurePaymentProcessor {
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final Logger LOGGER = Logger.getLogger(SecurePaymentProcessor.class.getName());
    private static final String LOG_FILE_PATH = "logs/payment_processor.log";

    public static void main(String[] args) {
        configureLogger();
        Console console = System.console();
        if (console == null) {
            LOGGER.severe("No console available. Exiting.");
            System.exit(1);
        }

        try {
            String cardNumber = new String(console.readPassword("Enter Card Number: "));
            String cardCVC = new String(console.readPassword("Enter CVC: "));

            if (!validateCardDetails(cardNumber, cardCVC)) {
                LOGGER.warning("Invalid card details provided.");
                System.exit(1);
            }

            String encryptedData = encryptPaymentData(cardNumber + ":" + cardCVC);
            LOGGER.info("Payment data encrypted successfully.");
            System.out.println("Encrypted Payment Data: " + encryptedData);

            String decryptedData = decryptPaymentData(encryptedData);
            LOGGER.info("Payment data decrypted successfully.");
            System.out.println("Decrypted Payment Data: " + decryptedData);

            logAuditTrail("Payment data processed successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred: ", e);
        }
    }

    private static boolean validateCardDetails(String cardNumber, String cvc) {
        if (cardNumber == null || cvc == null) return false;
        return cardNumber.matches("\\d{16}") && cvc.matches("\\d{3}");
    }

    private static String encryptPaymentData(String paymentData) throws GeneralSecurityException {
        byte[] iv = generateIV();
        SecretKey key = generateAESKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encryptedBytes = cipher.doFinal(paymentData.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        saveEncryptionKey(key);

        return Base64.getEncoder().encodeToString(combined);
    }

    private static String decryptPaymentData(String encryptedData) throws GeneralSecurityException {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

        byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKey key = loadEncryptionKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static void saveEncryptionKey(SecretKey key) {
        LOGGER.info("Encryption key securely stored (simulated). Expand to integrate with a secure key management system.");
    }

    private static SecretKey loadEncryptionKey() {
        LOGGER.info("Encryption key securely retrieved (simulated). Expand to integrate with a secure key management system.");
        try {
            return generateAESKey();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Failed to regenerate key: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void logAuditTrail(String message) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String logEntry = timestamp + " - AUDIT: " + message;
            Files.write(Paths.get(LOG_FILE_PATH), (logEntry + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOGGER.info("Audit trail logged.");
        } catch (Exception e) {
            LOGGER.severe("Failed to log audit trail: " + e.getMessage());
        }
    }

    private static void configureLogger() {
        try {
            LogManager.getLogManager().reset();

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);

            FileHandler fileHandler = new FileHandler(LOG_FILE_PATH, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(consoleHandler);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
            LOGGER.setUseParentHandlers(false);

            LOGGER.info("Logger configured successfully.");
        } catch (Exception e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }
}