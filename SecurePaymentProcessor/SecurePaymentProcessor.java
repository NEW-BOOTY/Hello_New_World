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

public class SecurePaymentProcessor {
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            System.err.println("No console available. Exiting.");
            System.exit(1);
        }

        try {
            // Capture sensitive payment information
            String cardNumber = new String(console.readPassword("Enter Card Number: "));
            String cardCVC = new String(console.readPassword("Enter CVC: "));

            // Secure data encryption
            String encryptedData = encryptPaymentData(cardNumber + ":" + cardCVC);
            System.out.println("Encrypted Payment Data: " + encryptedData);

            // Decryption simulation (for demonstration)
            String decryptedData = decryptPaymentData(encryptedData);
            System.out.println("Decrypted Payment Data: " + decryptedData);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String encryptPaymentData(String paymentData) throws GeneralSecurityException {
        byte[] iv = generateIV();
        SecretKey key = generateAESKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encryptedBytes = cipher.doFinal(paymentData.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    private static String decryptPaymentData(String encryptedData) throws GeneralSecurityException {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

        byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKey key = generateAESKey();
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
}
