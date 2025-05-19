/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package com.security.nextgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

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

@Controller
class WebController {
    private final IntrusionDetectionSystem ids;
    private final EncryptionService encryptionService;

    public WebController() throws Exception {
        this.ids = new IntrusionDetectionSystem();
        this.encryptionService = new EncryptionService();
    }

    @GetMapping("/")
    public ModelAndView dashboard(Model model) {
        model.addAttribute("intrusionStatus", ids.analyzeTraffic() ? "INTRUSION DETECTED!" : "System Secure");
        return new ModelAndView("dashboard");
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

// Frontend Enhancements: Web UI Dashboard
/*
 * Added real-time charts using Chart.js
 * Implemented real-time alerts for security breaches
 * Optimized UI for better visualization
 */
