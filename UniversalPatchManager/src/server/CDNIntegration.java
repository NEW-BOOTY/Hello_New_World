/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package server;

public class CDNIntegration {

    private String cdnUrl;

    public CDNIntegration(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }

    public void fetchPatch(String patchName) {
        System.out.println("Fetching patch: " + patchName + " from CDN: " + cdnUrl);
        // Implement actual patch fetching logic here
    }
}
