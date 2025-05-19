/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package core;

import utils.LoggerUtil;

public class RollbackHandler {

    public void rollbackPatch(String patchName) {
        System.out.println("Rolling back patch: " + patchName);
        LoggerUtil.log("Patch rolled back: " + patchName);
    }
}
