// Copyright © 2024 Devin B. Royal. All Rights Reserved.
package com.example.riskyoperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiskyOperationController {

    private static final Logger logger = LoggerFactory.getLogger(RiskyOperationController.class);

    @GetMapping("/performRiskyOperation")
    public ResponseEntity<?> performRiskyOperation() {
        try {
            // Example of a risky operation
            int result = 1 / 0; // Simulate an arithmetic error
            return ResponseEntity.ok("Risky operation result: " + result);
        } catch (ArithmeticException e) {
            logger.error("ArithmeticException occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Division by zero is not allowed.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }
}
