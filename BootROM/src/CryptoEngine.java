/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.security.SecureRandom;
import java.util.logging.*;

public class CryptoEngine {
    private static final Logger logger = Logger.getLogger(CryptoEngine.class.getName());

    public void generateSecureKey() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] key = new byte[16];
            secureRandom.nextBytes(key);
            logger.info("Secure Key Generated: " + bytesToHex(key));
        } catch (Exception e) {
            ErrorHandler.logError("Cryptographic Engine Failure", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}
