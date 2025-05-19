/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;

public class VirtualStorageSystem {

    // Define virtual storage parameters
    private static final String STORAGE_PATH = "virtual_storage.dat";
    private static final long VIRTUAL_STORAGE_SIZE = 1L * 1024 * 1024 * 1024 * 1024 * 1024; // 1 Petabyte
    private static final int BLOCK_SIZE = 4096; // Block size of 4 KB

    public static void main(String[] args) {
        try {
            initializeStorage();
            System.out.println("Virtual Storage System Initialized.");

            // Demonstration of file operations
            writeFile("example.txt", "This is a test file.");
            String content = readFile("example.txt");
            System.out.println("Read from virtual file: " + content);

            deleteFile("example.txt");
            System.out.println("File deleted successfully.");

        } catch (IOException e) {
            System.err.println("Error initializing virtual storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize the virtual storage system
    private static void initializeStorage() throws IOException {
        File file = new File(STORAGE_PATH);
        if (!file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(VIRTUAL_STORAGE_SIZE);
            }
        }
    }

    // Write data to a virtual file
    private static void writeFile(String fileName, String content) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(STORAGE_PATH, "rw");
             FileChannel channel = raf.getChannel()) {
            long position = getFilePosition(fileName);
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
            channel.position(position);
            channel.write(buffer);
        }
    }

    // Read data from a virtual file
    private static String readFile(String fileName) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(STORAGE_PATH, "r");
             FileChannel channel = raf.getChannel()) {
            long position = getFilePosition(fileName);
            ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
            channel.position(position);
            channel.read(buffer);
            buffer.flip();
            return new String(buffer.array(), buffer.position(), buffer.remaining()).trim();
        }
    }

    // Delete a virtual file (overwrite with zeros)
    private static void deleteFile(String fileName) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(STORAGE_PATH, "rw");
             FileChannel channel = raf.getChannel()) {
            long position = getFilePosition(fileName);
            ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
            Arrays.fill(buffer.array(), (byte) 0);
            channel.position(position);
            channel.write(buffer);
        }
    }

    // Mimics file position calculation in storage
    private static long getFilePosition(String fileName) {
        return Math.abs(fileName.hashCode()) % (VIRTUAL_STORAGE_SIZE / BLOCK_SIZE) * BLOCK_SIZE;
    }
}
