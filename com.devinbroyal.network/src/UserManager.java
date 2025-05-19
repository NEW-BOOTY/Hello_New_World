/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
package com.devinroyal.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManager {
    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());
    private final DatabaseManager dbManager;

    public UserManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, new PasswordManager().hashPassword(password));
            stmt.executeUpdate();
            System.out.println("User added successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
        }
    }

    public void deletePassword(String username) {
        String sql = "UPDATE users SET password = NULL WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Password deleted successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting password", e);
        }
    }

    public void resetPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, new PasswordManager().hashPassword(newPassword));
            stmt.setString(2, username);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Password reset successfully. New password: " + newPassword);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error resetting password", e);
        }
    }
}
