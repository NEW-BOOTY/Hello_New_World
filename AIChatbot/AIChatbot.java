/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AIChatbot {
    private static Map<String, String> responses = new HashMap<>();

    static {
        responses.put("hello", "Hello! How can I assist you today?");
        responses.put("help", "Sure, I'm here to help. What do you need assistance with?");
        responses.put("search", "You can use our search engine to find information. Just type your query.");
        // Add more responses as needed
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the AI Chatbot. Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("exit")) {
                break;
            }
            String response = getResponse(input);
            System.out.println("Bot: " + response);
        }
    }

    private static String getResponse(String input) {
        for (String key : responses.keySet()) {
            if (input.contains(key)) {
                return responses.get(key);
            }
        }
        return "I'm sorry, I don't understand that. Can you please rephrase?";
    }
}