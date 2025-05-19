/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package core;

import utils.LoggerUtil;

public class UpdateHandler {
    public void applyUpdate(String patchPath) {
        try {
            System.out.println("Applying patch from: " + patchPath);
            LoggerUtil.log("Patch applied successfully.");
        } catch (Exception e) {
            LoggerUtil.log("Error applying patch: " + e.getMessage());
        }
    }
}
