/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class UniversalPatchManager {

    public static void main(String[] args) {
        try {
            // Detect the operating system
            String os = detectOperatingSystem();
            System.out.println("Operating System Detected: " + os);

            // Fetch available updates for the OS
            List<String> updates = fetchUpdates(os);
            if (updates.isEmpty()) {
                System.out.println("No updates available for your operating system.");
                return;
            }

            // Display updates and prompt for installation
            System.out.println("Available Updates:");
            for (int i = 0; i < updates.size(); i++) {
                System.out.println((i + 1) + ". " + updates.get(i));
            }

            System.out.print("Enter the number of the update to install (0 to exit): ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            if (choice == 0 || choice > updates.size()) {
                System.out.println("Exiting without installing updates.");
                return;
            }

            // Download and apply the selected update
            String selectedUpdate = updates.get(choice - 1);
            System.out.println("Downloading update: " + selectedUpdate);
            File updateFile = downloadUpdate(selectedUpdate);

            System.out.println("Applying update...");
            applyUpdate(updateFile, os);

            System.out.println("Update applied successfully!");

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Detect the operating system
    private static String detectOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "Windows";
        if (os.contains("mac")) return "macOS";
        if (os.contains("nix") || os.contains("nux") || os.contains("aix")) return "Linux";
        throw new UnsupportedOperationException("Unsupported operating system: " + os);
    }

    // Fetch updates for the detected OS
    private static List<String> fetchUpdates(String os) throws IOException {
        // Placeholder: Replace with actual update server URL
        String updateServerUrl = "https://example.com/updates/" + os.toLowerCase();
        System.out.println("Fetching updates from: " + updateServerUrl);

        URL url = new URL(updateServerUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        List<String> updates = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            updates.add(line);
        }
        in.close();

        return updates;
    }

    // Download the selected update
    private static File downloadUpdate(String updateUrl) throws IOException {
        // Placeholder: Replace with actual file download logic
        String localFileName = "update.patch";
        Files.copy(new URL(updateUrl).openStream(), Paths.get(localFileName), StandardCopyOption.REPLACE_EXISTING);
        return new File(localFileName);
    }

    // Apply the update
    private static void applyUpdate(File updateFile, String os) throws IOException {
        if (os.equals("Windows")) {
            // Windows-specific patch application logic
            Runtime.getRuntime().exec("cmd.exe /c " + updateFile.getAbsolutePath());
        } else if (os.equals("macOS")) {
            // macOS-specific patch application logic
            Runtime.getRuntime().exec("sh " + updateFile.getAbsolutePath());
        } else if (os.equals("Linux")) {
            // Linux-specific patch application logic
            Runtime.getRuntime().exec("bash " + updateFile.getAbsolutePath());
        } else {
            throw new UnsupportedOperationException("Patch application not supported for OS: " + os);
        }
    }
}

--------------------------------------------------------------------------------------------------

/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.*;

public class UniversalPatchManagerGUI extends Application {

    private static final Logger LOGGER = Logger.getLogger(UniversalPatchManagerGUI.class.getName());
    private static final String LOG_FILE = "update_log.txt";
    private static final String UPDATE_SERVER_BASE_URL = "https://secure-update-server.com/updates/";

    public static void main(String[] args) {
        setupLogger();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Universal Patch Manager");

        // GUI Components
        Label osLabel = new Label("Detecting Operating System...");
        Button detectOSButton = new Button("Detect OS");
        ListView<String> updateList = new ListView<>();
        Button fetchUpdatesButton = new Button("Fetch Updates");
        Button installUpdateButton = new Button("Install Update");

        VBox layout = new VBox(10, osLabel, detectOSButton, updateList, fetchUpdatesButton, installUpdateButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Event Handlers
        detectOSButton.setOnAction(e -> {
            try {
                String os = detectOperatingSystem();
                osLabel.setText("Detected OS: " + os);
                LOGGER.info("Operating System detected: " + os);
            } catch (Exception ex) {
                showErrorDialog("Error detecting OS", ex.getMessage());
                LOGGER.severe("Error detecting OS: " + ex.getMessage());
            }
        });

        fetchUpdatesButton.setOnAction(e -> {
            try {
                String os = osLabel.getText().replace("Detected OS: ", "").trim();
                if (os.isEmpty()) {
                    showErrorDialog("Error", "Please detect the operating system first.");
                    return;
                }

                List<String> updates = fetchUpdates(os);
                updateList.getItems().setAll(updates);
                LOGGER.info("Updates fetched: " + updates);
            } catch (Exception ex) {
                showErrorDialog("Error fetching updates", ex.getMessage());
                LOGGER.severe("Error fetching updates: " + ex.getMessage());
            }
        });

        installUpdateButton.setOnAction(e -> {
            try {
                String selectedUpdate = updateList.getSelectionModel().getSelectedItem();
                if (selectedUpdate == null) {
                    showErrorDialog("Error", "Please select an update to install.");
                    return;
                }

                File updateFile = downloadUpdate(selectedUpdate);
                applyUpdate(updateFile);
                showInfoDialog("Success", "Update installed successfully!");
                LOGGER.info("Update installed successfully: " + selectedUpdate);
            } catch (Exception ex) {
                showErrorDialog("Error installing update", ex.getMessage());
                LOGGER.severe("Error installing update: " + ex.getMessage());
                rollbackUpdate();
            }
        });
    }

    // Detect the operating system
    private static String detectOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "Windows";
        if (os.contains("mac")) return "macOS";
        if (os.contains("nix") || os.contains("nux") || os.contains("aix")) return "Linux";
        throw new UnsupportedOperationException("Unsupported operating system: " + os);
    }

    // Fetch updates securely using HTTPS
    private static List<String> fetchUpdates(String os) throws IOException {
        URL url = new URL(UPDATE_SERVER_BASE_URL + os.toLowerCase());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to fetch updates. HTTP Code: " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        List<String> updates = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            updates.add(line);
        }
        reader.close();

        return updates;
    }

    // Download update securely
    private static File downloadUpdate(String updateUrl) throws IOException {
        URL url = new URL(updateUrl);
        File tempFile = File.createTempFile("update", ".patch");
        try (InputStream in = url.openStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    // Apply the update
    private static void applyUpdate(File updateFile) throws IOException {
        String os = detectOperatingSystem();
        if (os.equals("Windows")) {
            Runtime.getRuntime().exec("cmd.exe /c " + updateFile.getAbsolutePath());
        } else if (os.equals("macOS") || os.equals("Linux")) {
            Runtime.getRuntime().exec("sh " + updateFile.getAbsolutePath());
        } else {
            throw new UnsupportedOperationException("Patch application not supported for OS: " + os);
        }
    }

    // Rollback in case of failure
    private static void rollbackUpdate() {
        LOGGER.warning("Rolling back to previous state...");
        // Logic to revert any partial changes can be added here
    }

    // Setup Logger
    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }

    // Utility: Show Error Dialog
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Utility: Show Info Dialog
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
