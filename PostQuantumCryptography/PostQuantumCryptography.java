/*
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 *
 * Post-Quantum Cryptography (NTRU & Kyber)
 * This implementation demonstrates the use of NTRU and Kyber algorithms for real-world security.
 */

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.PublicKeyParameter;
import org.bouncycastle.pqc.crypto.kyber.Kyber;
import org.bouncycastle.pqc.crypto.ntru.NTRUEncryption;
import org.bouncycastle.util.encoders.Base64;

public class PostQuantumCryptography {
  // NTRU encryption and decryption
  public static String ntruEncrypt(String message, PublicKeyParameter publicKey)
      throws CryptoException {
    NTRUEncryption ntru = new NTRUEncryption();
    ntru.init(true, publicKey);
    byte[] encryptedMessage = ntru.encrypt(message.getBytes());
    return Base64.toBase64String(encryptedMessage);
  }

  public static String ntruDecrypt(String encryptedMessage, AsymmetricKeyParameter privateKey)
      throws CryptoException {
    NTRUEncryption ntru = new NTRUEncryption();
    ntru.init(false, privateKey);
    byte[] decryptedMessage = ntru.decrypt(Base64.decode(encryptedMessage));
    return new String(decryptedMessage);
  }

  // Kyber encryption and decryption
  public static String kyberEncrypt(String message, PublicKeyParameter publicKey)
      throws CryptoException {
    Kyber kyber = new Kyber();
    kyber.init(true, publicKey);
    byte[] encryptedMessage = kyber.encrypt(message.getBytes());
    return Base64.toBase64String(encryptedMessage);
  }

  public static String kyberDecrypt(String encryptedMessage, AsymmetricKeyParameter privateKey)
      throws CryptoException {
    Kyber kyber = new Kyber();
    kyber.init(false, privateKey);
    byte[] decryptedMessage = kyber.decrypt(Base64.decode(encryptedMessage));
    return new String(decryptedMessage);
  }

  public static void main(String[] args) throws CryptoException {
    // Example usage of NTRU and Kyber for encryption and decryption
    String message = "This is a secure message!";

    // NTRU Example
    String encryptedMessageNTRU = ntruEncrypt(message, publicKey); // publicKey should be defined
    String decryptedMessageNTRU =
        ntruDecrypt(encryptedMessageNTRU, privateKey); // privateKey should be defined

    System.out.println("Decrypted NTRU Message: " + decryptedMessageNTRU);

    // Kyber Example
    String encryptedMessageKyber = kyberEncrypt(message, publicKey); // publicKey should be defined
    String decryptedMessageKyber =
        kyberDecrypt(encryptedMessageKyber, privateKey); // privateKey should be defined

    System.out.println("Decrypted Kyber Message: " + decryptedMessageKyber);
  }
}
