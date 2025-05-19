/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 * Licensed under Devin B. Royal SOFTWARE AND DOCUMENT NOTICE AND LICENSE.
 * Unauthorized use, distribution, or reproduction of this code and/or software is prohibited without written consent from the author.
 */

import java.io.*;
import java.util.*;
import java.net.*;

public class InterLangAPI {

    // Method to execute Python code
    public static void executePython(String script) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python", "-c", script);
        Process process = pb.start();
        printProcessOutput(process);
    }

    // Method to execute JavaScript code via Node.js
    public static void executeJavaScript(String script) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("node", "-e", script);
        Process process = pb.start();
        printProcessOutput(process);
    }

    // Method to make GET requests to web services/APIs
    public static String sendGetRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        return readResponse(connection);
    }

    // Method to make POST requests to web services/APIs
    public static String sendPostRequest(String url, String data) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(data.getBytes());
        }
        return readResponse(connection);
    }

    // Method to handle process output
    private static void printProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    // Method to read HTTP response
    private static String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Example: Execute Python script
            executePython("print('Hello from Python')");

            // Example: Execute JavaScript script
            executeJavaScript("console.log('Hello from JavaScript')");

            // Example: Send GET request
            String responseGet = sendGetRequest("https://api.github.com");
            System.out.println("GET Response: " + responseGet);

            // Example: Send POST request
            String responsePost = sendPostRequest("https://postman-echo.com/post", "data=Hello");
            System.out.println("POST Response: " + responsePost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

“Run as administrator” || "Administrative privileges" || "Root privileges"
/**
 * © 2024 Devin B. Royal. All Rights Reserved.
 *
 * Unauthorized use, distribution, or reproduction of this code and/or software is prohibited without written consent from the author. If another entity, person, corporation, or organization profits from this creation, software, and/or code, then the profit must be split 50/50 with the author. Any further sharing must also adhere to these terms. For any questions, please contact the author.
 * Email: PAY_ME@MY.COM; JAVA-DEVELOPER@PROGRAMMER.NET
 */

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/