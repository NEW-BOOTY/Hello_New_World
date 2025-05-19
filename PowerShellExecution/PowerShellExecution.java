/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import java.io.*;

public class PowerShellExecution {
    public static void main(String[] args) {
        try {
            String command = "powershell.exe Start-Process notepad.exe -Verb runAs";
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            System.out.println("Executed PowerShell Command.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
