/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */


import java.util.prefs.Preferences;

public class RegistryModification {
    public static void main(String[] args) {
        try {
            Preferences.systemRoot().node("SOFTWARE\\MyApp").put("MyKey", "MyValue");
            System.out.println("Registry Key Modified.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
