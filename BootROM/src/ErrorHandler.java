/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.util.logging.*;

public class ErrorHandler {
    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());

    public static void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
