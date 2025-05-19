/**
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static void main(String[] args) {
        // Database configuration properties
        String dbUrl = "jdbc:mysql://localhost:3306/yourDatabaseName";
        String dbUsername = "yourUsername";
        String dbPassword = "yourPassword";

        // Establishing a connection using try-with-resources
        try (Connection connection = getDatabaseConnection(dbUrl, dbUsername, dbPassword)) {
            if (connection != null) {
                System.out.println("Database connection established successfully.");
                // Perform database operations here
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    /**
     * Establish a connection to the database.
     *
     * @param url      Database URL
     * @param username Database username
     * @param password Database password
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    private static Connection getDatabaseConnection(String url, String username, String password) throws SQLException {
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("Driver loading failed", e);
        }

        // Create and return the database connection
        return DriverManager.getConnection(url, username, password);
    }
}
