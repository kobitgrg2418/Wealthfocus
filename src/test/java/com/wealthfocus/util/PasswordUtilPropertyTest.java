package com.wealthfocus.util;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for PasswordUtil.
 * These tests validate universal properties that should hold for all valid password inputs.
 */
class PasswordUtilPropertyTest {

    /**
     * Property 7: Password Hashing
     * 
     * **Validates: Requirements 2.1, 2.2**
     * 
     * For any password provided during registration, the system SHALL store a BCrypt hash
     * in the database, not the plain text password.
     * 
     * This property verifies two critical security requirements:
     * 1. Hashed passwords are NEVER equal to plain text passwords
     * 2. The same password produces different hashes (salt verification)
     */
    @Property
    @Label("Property 7: Password Hashing - Hashes never equal plain text")
    void hashedPasswordsNeverEqualPlainText(@ForAll("validPassword") String password) {
        // Act: Hash the password
        String hash = PasswordUtil.hash(password);
        
        // Assert: Hash should never equal the plain text password
        assertNotNull(hash, "Hash should not be null");
        assertFalse(hash.isEmpty(), "Hash should not be empty");
        assertNotEquals(password, hash, 
            "Hashed password must never equal plain text password (Requirement 2.2)");
        
        // Additional verification: Hash should look like a BCrypt hash
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
            "Hash should be in BCrypt format");
        assertTrue(hash.length() == 60, 
            "BCrypt hash should be exactly 60 characters long");
    }

    /**
     * Property 7: Password Hashing - Same password produces different hashes
     * 
     * **Validates: Requirements 2.1, 2.2**
     * 
     * This property verifies that BCrypt generates unique salts for each hash operation.
     * Even when hashing the same password multiple times, each hash should be different
     * due to the unique salt. This is a critical security feature that prevents rainbow
     * table attacks and makes it impossible to identify users with the same password.
     */
    @Property
    @Label("Property 7: Password Hashing - Same password produces different hashes (salt verification)")
    void samePasswordProducesDifferentHashes(@ForAll("validPassword") String password) {
        // Act: Hash the same password multiple times
        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);
        String hash3 = PasswordUtil.hash(password);
        
        // Assert: All hashes should be different due to unique salts
        assertNotEquals(hash1, hash2, 
            "Same password should produce different hashes due to unique salt generation (Requirement 2.1)");
        assertNotEquals(hash2, hash3, 
            "Same password should produce different hashes due to unique salt generation (Requirement 2.1)");
        assertNotEquals(hash1, hash3, 
            "Same password should produce different hashes due to unique salt generation (Requirement 2.1)");
        
        // Assert: All hashes should be valid BCrypt hashes
        assertTrue(hash1.startsWith("$2a$") || hash1.startsWith("$2b$") || hash1.startsWith("$2y$"),
            "First hash should be in BCrypt format");
        assertTrue(hash2.startsWith("$2a$") || hash2.startsWith("$2b$") || hash2.startsWith("$2y$"),
            "Second hash should be in BCrypt format");
        assertTrue(hash3.startsWith("$2a$") || hash3.startsWith("$2b$") || hash3.startsWith("$2y$"),
            "Third hash should be in BCrypt format");
        
        // Assert: Despite different hashes, all should verify against the original password
        assertTrue(PasswordUtil.verify(password, hash1), 
            "Original password should verify against first hash");
        assertTrue(PasswordUtil.verify(password, hash2), 
            "Original password should verify against second hash");
        assertTrue(PasswordUtil.verify(password, hash3), 
            "Original password should verify against third hash");
    }

    /**
     * Property 7: Password Hashing - Hash format consistency
     * 
     * **Validates: Requirements 2.1, 2.2**
     * 
     * This property verifies that all generated hashes follow the BCrypt format
     * consistently across different password inputs.
     */
    @Property
    @Label("Property 7: Password Hashing - Hash format consistency")
    void hashFormatConsistency(@ForAll("validPassword") String password) {
        // Act: Hash the password
        String hash = PasswordUtil.hash(password);
        
        // Assert: Hash should follow BCrypt format
        assertNotNull(hash, "Hash should not be null");
        assertEquals(60, hash.length(), 
            "BCrypt hash should be exactly 60 characters long");
        
        // BCrypt format: $2a$12$[22 char salt][31 char hash]
        // The hash should start with version identifier
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
            "Hash should start with BCrypt version identifier ($2a$, $2b$, or $2y$)");
        
        // Verify work factor is embedded in the hash (should be $2a$12$ for our implementation)
        String[] parts = hash.split("\\$");
        assertTrue(parts.length >= 4, "BCrypt hash should have at least 4 parts separated by $");
        
        // The work factor should be present (we use 12)
        try {
            int workFactor = Integer.parseInt(parts[2]);
            assertTrue(workFactor > 0 && workFactor <= 31, 
                "Work factor should be between 1 and 31");
        } catch (NumberFormatException e) {
            fail("Work factor should be a valid integer");
        }
    }

    /**
     * Property 8: Password Verification
     * 
     * **Validates: Requirements 2.3, 3.3**
     * 
     * For any registered user, login SHALL succeed when the correct password is provided
     * and SHALL fail when an incorrect password is provided, using BCrypt verification.
     * 
     * This property verifies that correct passwords always verify successfully against
     * their corresponding hashes.
     */
    @Property
    @Label("Property 8: Password Verification - Correct passwords verify successfully")
    void correctPasswordVerifiesSuccessfully(@ForAll("validPassword") String password) {
        // Arrange: Hash the password
        String hash = PasswordUtil.hash(password);
        
        // Act: Verify the correct password against the hash
        boolean result = PasswordUtil.verify(password, hash);
        
        // Assert: Correct password should always verify successfully
        assertTrue(result, 
            "Correct password must verify successfully against its hash (Requirements 2.3, 3.3)");
    }

    /**
     * Property 8: Password Verification - Incorrect passwords fail verification
     * 
     * **Validates: Requirements 2.3, 3.3**
     * 
     * This property verifies that incorrect passwords always fail verification.
     * We test this by ensuring that a password verifies against its own hash but
     * not against a hash of a different password.
     */
    @Property
    @Label("Property 8: Password Verification - Incorrect passwords fail verification")
    void incorrectPasswordFailsVerification(
            @ForAll("validPassword") String correctPassword,
            @ForAll("validPassword") String incorrectPassword) {
        // Assume: The two passwords are different
        Assume.that(!correctPassword.equals(incorrectPassword));
        
        // Arrange: Hash the correct password
        String hash = PasswordUtil.hash(correctPassword);
        
        // Act: Try to verify the incorrect password against the hash
        boolean result = PasswordUtil.verify(incorrectPassword, hash);
        
        // Assert: Incorrect password should always fail verification
        assertFalse(result, 
            "Incorrect password must fail verification (Requirements 2.3, 3.3)");
    }

    /**
     * Property 8: Password Verification - Verification is consistent
     * 
     * **Validates: Requirements 2.3, 3.3**
     * 
     * This property verifies that password verification is deterministic and consistent.
     * Verifying the same password against the same hash multiple times should always
     * produce the same result.
     */
    @Property
    @Label("Property 8: Password Verification - Verification is consistent")
    void verificationIsConsistent(@ForAll("validPassword") String password) {
        // Arrange: Hash the password
        String hash = PasswordUtil.hash(password);
        
        // Act: Verify the password multiple times
        boolean result1 = PasswordUtil.verify(password, hash);
        boolean result2 = PasswordUtil.verify(password, hash);
        boolean result3 = PasswordUtil.verify(password, hash);
        
        // Assert: All verification attempts should produce the same result
        assertTrue(result1, "First verification should succeed");
        assertTrue(result2, "Second verification should succeed");
        assertTrue(result3, "Third verification should succeed");
        assertEquals(result1, result2, "Verification results should be consistent");
        assertEquals(result2, result3, "Verification results should be consistent");
    }

    /**
     * Property 8: Password Verification - Multiple hashes of same password all verify
     * 
     * **Validates: Requirements 2.3, 3.3**
     * 
     * This property verifies that even though the same password produces different hashes
     * (due to unique salts), the original password verifies successfully against all of them.
     */
    @Property
    @Label("Property 8: Password Verification - Multiple hashes of same password all verify")
    void multipleHashesOfSamePasswordAllVerify(@ForAll("validPassword") String password) {
        // Arrange: Create multiple hashes of the same password
        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);
        String hash3 = PasswordUtil.hash(password);
        
        // Assert: Hashes should be different (salt verification)
        assertNotEquals(hash1, hash2, "Different hashes should be generated");
        assertNotEquals(hash2, hash3, "Different hashes should be generated");
        
        // Act & Assert: Password should verify against all hashes
        assertTrue(PasswordUtil.verify(password, hash1), 
            "Password should verify against first hash");
        assertTrue(PasswordUtil.verify(password, hash2), 
            "Password should verify against second hash");
        assertTrue(PasswordUtil.verify(password, hash3), 
            "Password should verify against third hash");
    }

    // ========== Generators ==========

    /**
     * Generates valid passwords for testing.
     * Passwords should be non-empty strings with reasonable length and variety.
     * 
     * This generator creates passwords that include:
     * - Various lengths (from 1 to 100 characters)
     * - Alphanumeric characters
     * - Special characters
     * - Mixed case
     */
    @Provide
    Arbitrary<String> validPassword() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars("!@#$%^&*()_+-=[]{}|;:,.<>?")
                .ofMinLength(1)
                .ofMaxLength(100);
    }
}
