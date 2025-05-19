import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class DDoSProtectionApp {

    private static final Map<String, Integer> trafficTracker = new ConcurrentHashMap<>();
    private static final int ICMP_RATE_LIMIT = 100; // packets per second
    private static final Logger logger = Logger.getLogger(DDoSProtectionApp.class.getName());

    public static void main(String[] args) {
        logger.info("Starting DDoS Protection Application...");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Schedule traffic monitoring
        scheduler.scheduleAtFixedRate(DDoSProtectionApp::monitorTraffic, 0, 1, TimeUnit.SECONDS);

        // Schedule dynamic firewall updates
        scheduler.scheduleAtFixedRate(DDoSProtectionApp::updateFirewallRules, 0, 5, TimeUnit.SECONDS);

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down DDoS Protection Application...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }));
    }

    // Simulates monitoring of traffic
    private static void monitorTraffic() {
        Random random = new Random();
        String sourceIP = "192.168.1." + random.nextInt(255);
        int packetCount = random.nextInt(20);

        trafficTracker.merge(sourceIP, packetCount, Integer::sum);

        trafficTracker.forEach((ip, count) -> {
            if (count > ICMP_RATE_LIMIT) {
                logger.warning("Potential threat detected from IP: " + ip + " with " + count + " packets.");
                blockIP(ip);
            }
        });

        // Clear processed traffic data to avoid memory leaks
        trafficTracker.clear();
    }

    // Blocks IPs exceeding thresholds
    private static void blockIP(String ip) {
        logger.info("Blocking IP: " + ip);
        // Simulated firewall rule update
        // Example: executeCommand("iptables -A INPUT -s " + ip + " -j DROP");
    }

    // Updates firewall rules dynamically
    private static void updateFirewallRules() {
        logger.info("Updating firewall rules...");
        // Simulated rule configuration
        // Example: sync with external firewall management systems
    }

    // Executes shell commands for firewall rule updates (Linux example)
    private static void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            logger.severe("Error executing command: " + e.getMessage());
        }
    }
}
