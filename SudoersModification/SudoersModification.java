/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import java.io.*;

public class SudoersModification {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "echo 'user ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers");
            pb.inheritIO().start().waitFor();
            System.out.println("Sudoers file modified.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
