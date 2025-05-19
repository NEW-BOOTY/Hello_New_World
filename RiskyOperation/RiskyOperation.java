// Copyright © 2024 Devin B. Royal. All Rights Reserved.
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RiskyOperation {
    public static void main(String[] args) {
        performRiskyOperation("test_file.txt");
    }

    public static void performRiskyOperation(String filePath) {
        FileReader reader = null;
        try {
            // Attempt to read from a file
            File file = new File(filePath);
            reader = new FileReader(file);
            int data;
            while ((data = reader.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: File not found - " + filePath);
        } catch (IOException e) {
            System.out.println("IOException occurred while reading the file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            // Cleanup code
            try {
                if (reader != null) {
                    reader.close();
                    System.out.println("\nFile reader closed successfully.");
                }
            } catch (IOException e) {
                System.out.println("IOException occurred while closing the file reader: " + e.getMessage());
            }
        }
    }
}
