package com.wealthfocus.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class.
 * Tests basic password hashing and verification functionality.
 */
class PasswordUtilTest {
    
    @Test
    void testHashCreatesNonNullHash() {
        String password = "mySecurePassword123";
        String hash = PasswordUtil.hash(password);
        
        assertNotNull(hash, "Hash should not be null");
        assertFalse(hash.isEmpty(), "Hash should not be empty");
    }
    
    @Test
    void testHashedPasswordDifferentFromPlainText() {
        String password = "mySecurePassword123";
        String hash = PasswordUtil.hash(password);
        
        assertNotEquals(password, hash, "Hashed password should not equal plain text");
    }
    
    @Test
    void testSamePasswordProducesDifferentHashes() {
        String password = "mySecurePassword123";
        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);
        
        assertNotEquals(hash1, hash2, "Same password should produce different hashes due to unique salts");
    }
    
    @Test
    void testVerifyCorrectPassword() {
        String password = "mySecurePassword123";
        String hash = PasswordUtil.hash(password);
        
        assertTrue(PasswordUtil.verify(password, hash), "Correct password should verify successfully");
    }
    
    @Test
    void testVerifyIncorrectPassword() {
        String password = "mySecurePassword123";
        String wrongPassword = "wrongPassword456";
        String hash = PasswordUtil.hash(password);
        
        assertFalse(PasswordUtil.verify(wrongPassword, hash), "Incorrect password should fail verification");
    }
    
    @Test
    void testHashNullPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hash(null);
        }, "Hashing null password should throw IllegalArgumentException");
    }
    
    @Test
    void testHashEmptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hash("");
        }, "Hashing empty password should throw IllegalArgumentException");
    }
    
    @Test
    void testVerifyNullPlainPasswordThrowsException() {
        String hash = PasswordUtil.hash("password");
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verify(null, hash);
        }, "Verifying null plain password should throw IllegalArgumentException");
    }
    
    @Test
    void testVerifyNullHashedPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verify("password", null);
        }, "Verifying against null hash should throw IllegalArgumentException");
    }
    
    @Test
    void testVerifyEmptyPlainPasswordThrowsException() {
        String hash = PasswordUtil.hash("password");
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verify("", hash);
        }, "Verifying empty plain password should throw IllegalArgumentException");
    }
    
    @Test
    void testVerifyEmptyHashedPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verify("password", "");
        }, "Verifying against empty hash should throw IllegalArgumentException");
    }
    
    @Test
    void testVerifyInvalidHashReturnsFalse() {
        String password = "mySecurePassword123";
        String invalidHash = "not-a-valid-bcrypt-hash";
        
        assertFalse(PasswordUtil.verify(password, invalidHash), "Invalid hash should return false");
    }
}
