/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.util.logging.*;

public class BootManager {
    private static final Logger logger = Logger.getLogger(BootManager.class.getName());

    public static void main(String[] args) {
        logger.info("Starting Secure Boot System...");
        SecureLoader loader = new SecureLoader();

        if (!loader.verifyIntegrity()) {
            logger.severe("System Halted: Integrity Check Failed.");
            System.exit(1);
        }

        loader.initializeSystem();
    }
}
