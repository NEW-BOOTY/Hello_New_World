/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class TwofishEncryption {
    static {
        try {
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BouncyCastle provider: " + e.getMessage(), e);
        }
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, SecretKey secretKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Twofish/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
