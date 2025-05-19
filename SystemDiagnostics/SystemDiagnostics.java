/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class SystemDiagnostics {

    public static void main(String[] args) {
        try {
            // Display Storage Capacity
            long totalSpace = Files.getFileStore(Paths.get("/")).getTotalSpace();
            double totalSpacePB = (double) totalSpace / (1024 * 1024 * 1024 * 1024 * 1024);
            System.out.printf("Storage capacity: %.2f PB%n", totalSpacePB);

            // Display Network Interface Information
            System.out.println("\nNetwork Interface Details:");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback()) { // Only show active, non-loopback interfaces
                    System.out.println("Interface: " + ni.getDisplayName());
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address) {
                            System.out.println("IPv4 Address: " + address.getHostAddress());
                        } else if (address instanceof Inet6Address) {
                            System.out.println("IPv6 Address: " + address.getHostAddress());
                        }
                    }
                }
            }

            // Placeholder for Disk Speed Benchmark
            System.out.println("\nDisk Speed Test: Placeholder - Add benchmark logic here.");

            // Placeholder for Network Speed Benchmark
            System.out.println("Network Speed Test: Placeholder - Add benchmarking logic here.");

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
