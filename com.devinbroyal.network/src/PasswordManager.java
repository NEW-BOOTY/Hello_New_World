/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
package com.devinroyal.security;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

public class PasswordManager {

    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(ALLOWED_CHARS.length());
            password.append(ALLOWED_CHARS.charAt(index));
        }
        return password.toString();
    }
}
