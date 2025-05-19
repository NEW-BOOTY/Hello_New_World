/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;
import com.squareup.square.api.PaymentsApi;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SquarePaymentProcessor {

    private static final Logger logger = Logger.getLogger(SquarePaymentProcessor.class.getName());
    private static final String ACCESS_TOKEN = "YOUR_SQUARE_ACCESS_TOKEN"; // Replace with your Square Access Token
    private static final String LOCATION_ID = "YOUR_LOCATION_ID";          // Replace with your Location ID

    private final SquareClient squareClient;
    private final PaymentsApi paymentsApi;

    public SquarePaymentProcessor() {
        // Initialize the Square Client
        squareClient = new SquareClient.Builder()
                .environment(Environment.SANDBOX) // Change to Environment.PRODUCTION for live
                .accessToken(ACCESS_TOKEN)
                .build();

        paymentsApi = squareClient.getPaymentsApi();
    }

    /**
     * Processes a payment using the Square Payments API.
     *
     * @param sourceId    The payment source ID (e.g., card nonce or Cash App payment ID).
     * @param amountCents The payment amount in cents (e.g., 100 for $1.00).
     * @param currency    The currency code (e.g., "USD").
     * @return The payment response from Square.
     */
    public Payment processPayment(String sourceId, int amountCents, String currency) {
        try {
            // Create a unique ID for the payment request
            String idempotencyKey = UUID.randomUUID().toString();

            // Create the Money object
            Money money = new Money.Builder()
                    .amount((long) amountCents)
                    .currency(currency)
                    .build();

            // Build the payment request
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest.Builder(
                    sourceId,
                    idempotencyKey,
                    money
            ).locationId(LOCATION_ID).build();

            // Process the payment
            CreatePaymentResponse response = paymentsApi.createPayment(paymentRequest);

            // Log and return the payment result
            Payment payment = response.getPayment();
            logger.info("Payment Successful: " + payment);
            return payment;

        } catch (ApiException e) {
            logger.log(Level.SEVERE, "Square API Exception: " + e.getErrors(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected Exception: ", e);
        }
        return null;
    }

    public static void main(String[] args) {
        SquarePaymentProcessor processor = new SquarePaymentProcessor();

        // Replace with a valid source ID from your frontend
        String sourceId = "cnon:card-nonce-ok"; // Use a valid test or live nonce
        int amountCents = 5000; // $50.00
        String currency = "USD";

        // Process the payment
        Payment payment = processor.processPayment(sourceId, amountCents, currency);

        if (payment != null) {
            System.out.println("Payment ID: " + payment.getId());
            System.out.println("Status: " + payment.getStatus());
        } else {
            System.err.println("Payment failed. Check logs for details.");
        }
    }
}

/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

/**
 * SquarePaymentProcessor - A production-ready Java program for processing payments using the Square Payments API.
 * 
 * Capabilities:
 * - Initializes and configures the Square client with sandbox or production environment.
 * - Processes payments using a payment source ID (e.g., card nonce or Cash App payment ID).
 * - Handles money amounts and currency configurations.
 * - Generates unique idempotency keys for each payment request to ensure idempotency.
 * - Logs successful payments and handles exceptions with proper logging.
 * 
 * Usage:
 * - Replace `ACCESS_TOKEN` and `LOCATION_ID` with your Square account details.
 * - Initialize the SquarePaymentProcessor class and call the `processPayment` method with appropriate parameters.
 * - The program will log the payment result, including payment ID and status.
 */
