/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.security.nextgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@SpringBootApplication
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}

@RestController
@RequestMapping("/security")
class SecurityController {
    private static final Logger logger = Logger.getLogger(SecurityController.class.getName());
    private final IntrusionDetectionSystem ids = new IntrusionDetectionSystem();
    private final EncryptionService encryptionService = new EncryptionService();
    
    @PostMapping("/encrypt")
    public ResponseEntity<String> encryptData(@RequestBody String data) {
        try {
            String encryptedData = encryptionService.encrypt(data);
            return ResponseEntity.ok(encryptedData);
        } catch (Exception e) {
            logger.severe("Encryption failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Encryption error");
        }
    }
    
    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptData(@RequestBody String encryptedData) {
        try {
            String decryptedData = encryptionService.decrypt(encryptedData);
            return ResponseEntity.ok(decryptedData);
        } catch (Exception e) {
            logger.severe("Decryption failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Decryption error");
        }
    }
    
    @GetMapping("/detect")
    public ResponseEntity<String> detectIntrusion() {
        boolean intrusionDetected = ids.analyzeTraffic();
        if (intrusionDetected) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Intrusion detected! Security alert triggered.");
        } else {
            return ResponseEntity.ok("System secure.");
        }
    }
}

class EncryptionService {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final SecretKey secretKey;
    private final IvParameterSpec iv;

    public EncryptionService() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        this.secretKey = keyGenerator.generateKey();
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        this.iv = new IvParameterSpec(ivBytes);
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    }
}

class IntrusionDetectionSystem {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private boolean intrusionDetected = false;

    public IntrusionDetectionSystem() {
        scheduler.scheduleAtFixedRate(this::simulateIntrusionCheck, 0, 5, TimeUnit.SECONDS);
    }

    private void simulateIntrusionCheck() {
        intrusionDetected = random.nextDouble() < 0.1;
    }

    public boolean analyzeTraffic() {
        return intrusionDetected;
    }
}


/*
 * This is a sophisticated Spring Boot application focused on cybersecurity. Here's a breakdown of what this code does:
 *
 * Core Functionality:
 *
 *     Encryption and Decryption:
 *
 *         The EncryptionService class provides functionality to encrypt and decrypt data using the AES (Advanced Encryption Standard)
 *         algorithm in CBC (Cipher Block Chaining) mode with PKCS#5 padding for secure data handling.
 *
 *         It uses a secure key and initialization vector (IV) generated at runtime to ensure strong encryption.
 *
 *     Intrusion Detection:
 *
 *         The IntrusionDetectionSystem class simulates an intrusion detection mechanism by analyzing network traffic at scheduled 
 *         intervals. If a simulated intrusion is detected (based on a random probability), the system can respond accordingly.
 *
 * Spring Boot Integration:
 *
 *     The SecurityApplication class initializes the Spring Boot application.
 *
 *     The SecurityController class provides REST API endpoints to interact with the encryption service and intrusion detection system:
 *
 *         /encrypt (POST): Encrypts input data and returns the encrypted result.
 *
 *         /decrypt (POST): Decrypts encrypted data and returns the original text.
 *
 *         /detect (GET): Analyzes traffic to check for intrusions and returns the system's security status.
 *
 * Key Features:
 *
 *     EncryptionService:
 *
 *         Implements AES encryption and decryption with a 256-bit key.
 *
 *         Uses a secure random IV to protect against certain cryptographic attacks.
 *
 *     IntrusionDetectionSystem:
 *
 *         Periodically simulates intrusion checks using a background task (ScheduledExecutorService).
 *
 * Logging and Error Handling:
 *
 *     Uses Java's Logger to log errors and events.
 *
 *     Provides appropriate HTTP responses for both successful operations and errors.
 *
 * Scalability and Security:
 *
 *     The use of Spring Boot ensures the application is scalable and suitable for enterprise-level deployments.
 *
 *     Secure coding practices (like generating random keys and IVs) help mitigate vulnerabilities.
 *
 * Overall, this application simulates a basic cybersecurity tool, combining encryption, intrusion detection, and RESTful services.
 */
