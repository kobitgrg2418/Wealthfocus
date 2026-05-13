package com.wealthfocus.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil class.
 * 
 * Tests the email validation, non-empty checking, and password matching methods.
 */
class ValidationUtilTest {
    
    // Email validation tests
    
    @Test
    void testIsValidEmail_ValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("test.user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user_name@example.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("user-name@example-domain.com"));
        assertTrue(ValidationUtil.isValidEmail("user123@example123.com"));
    }
    
    @Test
    void testIsValidEmail_InvalidEmails() {
        assertFalse(ValidationUtil.isValidEmail("invalid"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
        assertFalse(ValidationUtil.isValidEmail("user@.com"));
        assertFalse(ValidationUtil.isValidEmail("user @example.com"));
        assertFalse(ValidationUtil.isValidEmail("user@example"));
    }
    
    @Test
    void testIsValidEmail_NullAndEmpty() {
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("   "));
    }
    
    @Test
    void testIsValidEmail_WithWhitespace() {
        assertTrue(ValidationUtil.isValidEmail("  user@example.com  "));
    }
    
    // Non-empty validation tests
    
    @Test
    void testIsNonEmpty_ValidStrings() {
        assertTrue(ValidationUtil.isNonEmpty("hello"));
        assertTrue(ValidationUtil.isNonEmpty("a"));
        assertTrue(ValidationUtil.isNonEmpty("  text  "));
    }
    
    @Test
    void testIsNonEmpty_NullAndEmpty() {
        assertFalse(ValidationUtil.isNonEmpty(null));
        assertFalse(ValidationUtil.isNonEmpty(""));
        assertFalse(ValidationUtil.isNonEmpty("   "));
        assertFalse(ValidationUtil.isNonEmpty("\t\n"));
    }
    
    // Password matching tests
    
    @Test
    void testPasswordsMatch_MatchingPasswords() {
        assertTrue(ValidationUtil.passwordsMatch("password123", "password123"));
        assertTrue(ValidationUtil.passwordsMatch("", ""));
        assertTrue(ValidationUtil.passwordsMatch("P@ssw0rd!", "P@ssw0rd!"));
    }
    
    @Test
    void testPasswordsMatch_NonMatchingPasswords() {
        assertFalse(ValidationUtil.passwordsMatch("password123", "password456"));
        assertFalse(ValidationUtil.passwordsMatch("Password", "password"));
        assertFalse(ValidationUtil.passwordsMatch("pass", "pass "));
    }
    
    @Test
    void testPasswordsMatch_NullValues() {
        assertFalse(ValidationUtil.passwordsMatch(null, "password"));
        assertFalse(ValidationUtil.passwordsMatch("password", null));
        assertFalse(ValidationUtil.passwordsMatch(null, null));
    }
    
    // Utility class instantiation test
    
    @Test
    void testCannotInstantiate() {
        assertThrows(UnsupportedOperationException.class, () -> {
            // Use reflection to try to instantiate the utility class
            java.lang.reflect.Constructor<ValidationUtil> constructor = 
                ValidationUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}
