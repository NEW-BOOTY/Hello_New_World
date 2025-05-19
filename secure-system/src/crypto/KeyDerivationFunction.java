/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class KeyDerivationFunction {
    public static SecretKey deriveKey(byte[] inputKeyMaterial, byte[] salt, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedKey = digest.digest(inputKeyMaterial);
        return new SecretKeySpec(hashedKey, algorithm);
    }
}
