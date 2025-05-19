public class OSIntegration {
    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows-specific logic
            System.out.println("Running on Windows.");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Unix/Linux/Mac-specific logic
            System.out.println("Running on Unix/Linux/Mac.");
        } else {
            System.out.println("Unsupported OS.");
        }
    }
}
