/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoggerUtil {

    private static final String LOG_FILE = "logs/update_log.txt";

    public static void log(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(message);
            System.out.println("Log: " + message);
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }
}
