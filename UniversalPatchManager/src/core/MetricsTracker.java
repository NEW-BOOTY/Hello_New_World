/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package core;

public class MetricsTracker {

    public void trackUpdateProgress(String patchName, double progress) {
        System.out.println("Tracking progress for patch: " + patchName + " - " + progress + "% completed");
    }
}
