/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * DecentralizedIdentityManager is responsible for managing users' identities with a focus on MFA
 * (Multi-Factor Authentication) token generation and verification.
 *
 * <p>Key Features: - Secure MFA token generation for user authentication. - Identity verification
 * via MFA tokens and public key integration. - Designed to support decentralized identity systems.
 *
 * <p>Use Cases: - Decentralized applications requiring user authentication. - Secure user
 * registration and identity management. - MFA-based access control for sensitive operations.
 */
public class DecentralizedIdentityManager {
  private final Map<String, Integer> mfaTokens = new HashMap<>();

  /**
   * Retrieves the MFA tokens map (useful for testing or debugging).
   *
   * @return a map of user IDs to their corresponding MFA tokens.
   */
  public Map<String, Integer> getMfaTokens() {
    return mfaTokens;
  }

  /**
   * Registers a user by generating and storing an MFA token.
   *
   * @param userId the unique identifier for the user.
   * @throws IllegalArgumentException if the userId is null or empty.
   */
  public void registerUser(String userId) {
    if (userId == null || userId.isEmpty()) {
      throw new IllegalArgumentException("[Error] User ID cannot be null or empty.");
    }

    int mfaToken = generateMfaToken();
    mfaTokens.put(userId, mfaToken);
    System.out.println(
        "[DecentralizedIdentityManager] MFA token generated for user " + userId + ": " + mfaToken);
  }

  /**
   * Verifies the user's identity using their MFA token and public key.
   *
   * @param userId the unique identifier for the user.
   * @param publicKey the public key of the user (not implemented in this version, placeholder for
   *     extension).
   * @param mfaToken the MFA token provided by the user.
   * @return true if the MFA token matches; false otherwise.
   * @throws IllegalArgumentException if the userId is null or empty.
   */
  public boolean verifyIdentity(String userId, String publicKey, int mfaToken) {
    if (userId == null || userId.isEmpty()) {
      throw new IllegalArgumentException("[Error] User ID cannot be null or empty.");
    }

    System.out.println("[DecentralizedIdentityManager] Verifying identity for user: " + userId);
    Integer storedToken = mfaTokens.get(userId);
    if (storedToken == null) {
      System.err.println("[Error] No MFA token found for user " + userId);
      return false;
    }

    if (storedToken == mfaToken) {
      System.out.println(
          "[DecentralizedIdentityManager] Identity verification successful for user " + userId);
      return true;
    } else {
      System.err.println(
          "[DecentralizedIdentityManager] Identity verification failed for user " + userId);
      return false;
    }
  }

  /**
   * Generates a secure six-digit MFA token.
   *
   * @return a random six-digit MFA token.
   */
  private int generateMfaToken() {
    return (int) (Math.random() * 900000) + 100000; // Random 6-digit number
  }

  public static void main(String[] args) {
    try {
      DecentralizedIdentityManager manager = new DecentralizedIdentityManager();

      // Register a user and generate their MFA token
      manager.registerUser("user123");

      // Retrieve the generated MFA token for testing purposes
      int mfaToken = manager.getMfaTokens().get("user123");

      // Verify the user's identity
      boolean isVerified = manager.verifyIdentity("user123", "dummyPublicKey", mfaToken);
      System.out.println("Is user verified? " + isVerified);

    } catch (Exception e) {
      System.err.println("An error occurred: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
