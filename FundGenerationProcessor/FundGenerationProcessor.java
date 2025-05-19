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
import java.util.*;
import java.util.concurrent.*;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;
import javax.net.ssl.*;
import java.util.stream.Collectors;

public class FundGenerationProcessor {
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final Logger LOGGER = Logger.getLogger(FundGenerationProcessor.class.getName());
    private static final String LOG_FILE_PATH = "logs/fund_generation_processor.log";

    private static final Map<String, Double> cardBalances = new HashMap<>();
    private static final Random random = new Random();

    public static void main(String[] args) {
        configureLogger();
        configureSSL();

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

            double generatedFunds = autoGenerateFunds();
            addFundsToCard(cardNumber, generatedFunds);

            logAuditTrail("Funds auto-generated and added successfully to card: " + cardNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred: ", e);
        }
    }

    private static boolean validateCardDetails(String cardNumber, String cvc) {
        if (cardNumber == null || cvc == null) return false;
        return cardNumber.matches("\\d{16}") && cvc.matches("\\d{3}");
    }

    private static double autoGenerateFunds() {
        List<String> sources = Arrays.asList("Loyalty Program", "Promotional Cashback", "Gift Cards", "Crowdsourcing Pool", "Miscellaneous Credits");
        Map<String, Double> generatedFundsMap = sources.stream()
            .collect(Collectors.toMap(source -> source, source -> random.nextDouble() * 100));

        double totalGeneratedFunds = generatedFundsMap.values().stream().mapToDouble(Double::doubleValue).sum();

        LOGGER.info("Generated funds from various sources: " + generatedFundsMap);
        System.out.println("Funds generated from sources: " + generatedFundsMap);
        return totalGeneratedFunds;
    }

    private static void addFundsToCard(String cardNumber, double amount) {
        cardBalances.putIfAbsent(cardNumber, 0.0);
        double currentBalance = cardBalances.get(cardNumber);
        cardBalances.put(cardNumber, currentBalance + amount);

        LOGGER.info("Added " + amount + " to card " + cardNumber + ". New balance: " + cardBalances.get(cardNumber));
        System.out.println("Funds added successfully. New balance: " + cardBalances.get(cardNumber));
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

    private static void configureSSL() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());

            SSLContext.setDefault(sslContext);
            LOGGER.info("SSL context configured successfully.");
        } catch (Exception e) {
            LOGGER.severe("Failed to configure SSL: " + e.getMessage());
        }
    }
}
