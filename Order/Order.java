/**
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */

import java.util.List;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Order {
    private static final Logger LOGGER = Logger.getLogger(Order.class.getName());
    
    private String orderId;
    private String userId;
    private String paymentMethod;
    private String shippingAddress;
    private String orderDate;
    private List<String> orderItems;

    public Order(String orderId, String userId, String paymentMethod, String shippingAddress, String orderDate, List<String> orderItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
        validate();
    }

    private void validate() {
        if (orderId == null || orderId.isEmpty()) {
            logAndThrow("Order ID cannot be null or empty");
        }
        if (userId == null || userId.isEmpty()) {
            logAndThrow("User ID cannot be null or empty");
        }
        if (paymentMethod == null || !isValidPaymentMethod(paymentMethod)) {
            logAndThrow("Invalid payment method: " + paymentMethod);
        }
        if (shippingAddress == null || shippingAddress.isEmpty()) {
            logAndThrow("Shipping address cannot be null or empty");
        }
        if (orderDate == null || !isValidDate(orderDate)) {
            logAndThrow("Invalid order date format: " + orderDate);
        }
        if (orderItems == null || orderItems.isEmpty()) {
            logAndThrow("Order items cannot be null or empty");
        }
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod.equals("Credit Card") || paymentMethod.equals("PayPal") || paymentMethod.equals("Bank Transfer");
    }

    private boolean isValidDate(String date) {
        // Basic date validation pattern, format: YYYY-MM-DD
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(datePattern, date);
    }

    private void logAndThrow(String message) {
        LOGGER.log(Level.SEVERE, message);
        throw new IllegalArgumentException(message);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public List<String> getOrderItems() {
        return orderItems;
    }

    public static void main(String[] args) {
        try {
            // Example of creating a valid order
            List<String> items = List.of("Item1", "Item2");
            Order order = new Order("12345", "user001", "Credit Card", "123 Main St, City, Country", "2024-08-06", items);
            System.out.println("Order created successfully: " + order.getOrderId());

            // Example of creating an invalid order
            Order invalidOrder = new Order("", "", "Cash", "", "20240806", items);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating order: " + e.getMessage());
        }
    }
}

/**
 * Enhancements:
Logging: Added Logger for structured error logging.
Validation:orderItems now checks for null or empty.
Error Handling: logAndThrow method to log errors and throw exceptions with detailed messages.
This enhanced version of the Order class improves error handling and validation, making it more robust and production-ready..
 */