/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UpdateServer {

    private int port;

    public UpdateServer(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Update server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Handle client request in a new thread or task
                System.out.println("Client connected: " + clientSocket.getInetAddress());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new UpdateServer(8080).startServer();
    }
}
