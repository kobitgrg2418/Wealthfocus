package com.wealthfocus.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt.
 * 
 * BCrypt is a password hashing function designed to be computationally expensive
 * to resist brute-force attacks. It automatically handles salt generation and
 * incorporates a work factor to adjust computational cost.
 */
public class PasswordUtil {
    
    /**
     * BCrypt work factor (log2 rounds). A work factor of 12 means 2^12 = 4096 rounds.
     * This provides a good balance between security and performance.
     * Higher values increase security but also increase computation time.
     */
    private static final int WORK_FACTOR = 12;
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Hashes a plain text password using BCrypt with automatic salt generation.
     * 
     * Each call to this method will produce a different hash for the same password
     * because BCrypt generates a unique salt for each hash. This is a security feature,
     * not a bug.
     * 
     * @param plainPassword the plain text password to hash
     * @return the BCrypt hash of the password, including the salt and work factor
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    
    /**
     * Verifies a plain text password against a BCrypt hash.
     * 
     * This method extracts the salt and work factor from the stored hash and
     * uses them to hash the provided plain password, then compares the results.
     * 
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the BCrypt hash to verify against
     * @return true if the password matches the hash, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // BCrypt.checkpw throws IllegalArgumentException if the hash is invalid
            return false;
        }
    }
}
