/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import java.io.*;

public class ShellExecution {
    public static void main(String[] args) {
        try {
            Process p = Runtime.getRuntime().exec("sudo -S -p '' echo root_password | sudo -S command");
            p.waitFor();
            System.out.println("Shell Command Executed as Root.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
