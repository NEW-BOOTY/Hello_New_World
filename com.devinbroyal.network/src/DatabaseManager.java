/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
package com.devinroyal.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:sqlite:./private_database.db";
    private static final String CONFIG_PATH = "./src/resources/config/application.properties";
    private Connection connection;

    public void initializeDatabase() {
        try {
            Properties props = new Properties();
            props.load(Files.newInputStream(Paths.get(CONFIG_PATH)));
            String encryptionKey = props.getProperty("db.encryption.key");

            if (encryptionKey == null || encryptionKey.isEmpty()) {
                throw new IllegalStateException("Encryption key not found in configuration file.");
            }

            connection = DriverManager.getConnection(DB_URL);
            try (PreparedStatement stmt = connection.prepareStatement("PRAGMA key = ?")) {
                stmt.setString(1, encryptionKey);
                stmt.execute();
            }
            createUserTable();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Database initialization failed.", e);
        }
    }

    private void createUserTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT
            )
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user table", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
