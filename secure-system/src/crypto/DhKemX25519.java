/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package crypto;

import java.security.*;
import javax.crypto.*;
import java.util.Base64;

public class DhKemX25519 {
    private KeyPair keyPair;

    public DhKemX25519() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("X25519");
        this.keyPair = keyPairGen.generateKeyPair();
    }

    public byte[] generateSharedSecret(PublicKey peerPublicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgree = KeyAgreement.getInstance("X25519");
        keyAgree.init(this.keyPair.getPrivate());
        keyAgree.doPhase(peerPublicKey, true);
        return keyAgree.generateSecret();
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public String encodeKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
