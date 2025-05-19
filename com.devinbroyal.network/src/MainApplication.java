/**
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */
package com.devinroyal;

import com.devinroyal.security.DatabaseManager;
import com.devinroyal.security.PasswordManager;
import com.devinroyal.security.UserManager;
import java.util.Scanner;

public class MainApplication {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.initializeDatabase();

            UserManager userManager = new UserManager(dbManager);
            PasswordManager passwordManager = new PasswordManager();

            System.out.println("Welcome to the Secure User Management System");
            System.out.println("1. Add User");
            System.out.println("2. Delete Password");
            System.out.println("3. Reset Password");
            System.out.println("4. Exit");

            while (true) {
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        userManager.addUser(username, password);
                        break;

                    case "2":
                        System.out.print("Enter username to delete password: ");
                        String delUsername = scanner.nextLine();
                        userManager.deletePassword(delUsername);
                        break;

                    case "3":
                        System.out.print("Enter username to reset password: ");
                        String resetUsername = scanner.nextLine();
                        String newPassword = passwordManager.generateRandomPassword();
                        userManager.resetPassword(resetUsername, newPassword);
                        break;

                    case "4":
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
