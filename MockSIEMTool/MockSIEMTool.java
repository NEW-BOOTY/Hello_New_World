/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class MockSIEMTool {

    // Mock Data (for simulating logs and events)
    private static List<String> eventLogs = new ArrayList<>();
    private static final String SPLUNK_API_URL = "https://your-splunk-instance.com:8089/services/search/jobs";
    private static final String SPLUNK_HEC_URL = "https://your-splunk-instance.com:8088";
    private static final String CHRONICLE_API_URL = "https://your-chronicle-instance.com/api/v1/query";
    private static final String SPLUNK_HEC_TOKEN = "your-splunk-hec-token";
    private static final String CHRONICLE_API_KEY = "your-chronicle-api-key";

    public static void main(String[] args) {
        try {
            // Simulating log generation
            generateMockEvents();

            // Simulate sending events to Splunk and Chronicle
            syncWithSplunk();
            syncWithChronicle();

            // Simulate searching and querying the logs
            searchLogs("error");
            searchLogs("login");

            // Simulate alerting on detected patterns
            simulateAlerts("error");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Simulate the generation of logs
    private static void generateMockEvents() {
        for (int i = 0; i < 10; i++) {
            String event = "Event #" + (i + 1) + ": Timestamp: " + System.currentTimeMillis() + ", Log Message: Log message with some errors and user logins.";
            eventLogs.add(event);
        }
        System.out.println("Generated mock event logs:");
        eventLogs.forEach(System.out::println);
    }

    // Sync events with Splunk
    public static void syncWithSplunk() throws IOException {
        // Simulating sending events to Splunk HEC
        for (String event : eventLogs) {
            sendToSplunkHEC(event);
        }

        // Simulating querying data from Splunk's REST API
        String query = "search index=_internal | head 10";
        String response = sendToSplunk(query);
        System.out.println("Splunk Response: " + response);
    }

    // Send data to Splunk using HTTP Event Collector (HEC)
    private static void sendToSplunkHEC(String eventData) throws IOException {
        URL url = new URL(SPLUNK_HEC_URL + "/services/collector/event");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Splunk " + SPLUNK_HEC_TOKEN);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Data sent to Splunk: " + response.toString());
        }
    }

    // Send a query request to Splunk
    private static String sendToSplunk(String query) throws IOException {
        URL url = new URL(SPLUNK_API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Splunk " + SPLUNK_HEC_TOKEN);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String data = "search=" + URLEncoder.encode(query, "UTF-8");
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    // Sync events with Chronicle
    public static void syncWithChronicle() throws IOException {
        for (String event : eventLogs) {
            String query = "{\"query\": \"INSERT INTO events VALUES ('" + event + "')\"}";
            String response = sendToChronicle(query);
            System.out.println("Chronicle Response: " + response);
        }
    }

    // Send a query request to Chronicle
    private static String sendToChronicle(String query) throws IOException {
        URL url = new URL(CHRONICLE_API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + CHRONICLE_API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = query.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    // Search logs for a specific pattern (e.g., "error", "login")
    private static void searchLogs(String pattern) {
        System.out.println("\nSearching logs for pattern: " + pattern);
        for (String log : eventLogs) {
            if (log.toLowerCase().contains(pattern.toLowerCase())) {
                System.out.println("Found match: " + log);
            }
        }
    }

    // Simulate alerting based on pattern detection (e.g., high number of "error" logs)
    private static void simulateAlerts(String pattern) {
        long matchCount = eventLogs.stream().filter(log -> log.toLowerCase().contains(pattern.toLowerCase())).count();
        if (matchCount > 3) {
            System.out.println("\n[ALERT] High volume of " + pattern + " detected! Total matches: " + matchCount);
        } else {
            System.out.println("\n[INFO] No alerts triggered for pattern: " + pattern);
        }
    }
}
