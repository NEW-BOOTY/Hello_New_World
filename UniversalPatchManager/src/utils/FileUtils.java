/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void createBackup(File file) throws IOException {
        File backupFile = new File(file.getAbsolutePath() + ".bak");
        if (file.renameTo(backupFile)) {
            System.out.println("Backup created successfully");
        } else {
            throw new IOException("Failed to create backup");
        }
    }
}
