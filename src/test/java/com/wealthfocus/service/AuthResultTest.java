package com.wealthfocus.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthResult class.
 * Validates that the result class correctly encapsulates authentication outcomes.
 */
class AuthResultTest {

    @Test
    void testSuccessResult() {
        String userId = "user123";
        AuthResult result = AuthResult.success(userId);
        
        assertTrue(result.isSuccess(), "Success result should have success=true");
        assertEquals(userId, result.getUserId(), "Success result should contain the user ID");
        assertNull(result.getErrorMessage(), "Success result should have null error message");
    }

    @Test
    void testFailureResult() {
        String errorMessage = "Invalid credentials";
        AuthResult result = AuthResult.failure(errorMessage);
        
        assertFalse(result.isSuccess(), "Failure result should have success=false");
        assertNull(result.getUserId(), "Failure result should have null user ID");
        assertEquals(errorMessage, result.getErrorMessage(), "Failure result should contain the error message");
    }

    @Test
    void testSuccessWithNullUserId() {
        AuthResult result = AuthResult.success(null);
        
        assertTrue(result.isSuccess(), "Success result should have success=true even with null userId");
        assertNull(result.getUserId(), "User ID should be null when passed as null");
        assertNull(result.getErrorMessage(), "Success result should have null error message");
    }

    @Test
    void testFailureWithNullErrorMessage() {
        AuthResult result = AuthResult.failure(null);
        
        assertFalse(result.isSuccess(), "Failure result should have success=false even with null error message");
        assertNull(result.getUserId(), "Failure result should have null user ID");
        assertNull(result.getErrorMessage(), "Error message should be null when passed as null");
    }

    @Test
    void testSuccessWithEmptyUserId() {
        String emptyUserId = "";
        AuthResult result = AuthResult.success(emptyUserId);
        
        assertTrue(result.isSuccess(), "Success result should have success=true");
        assertEquals(emptyUserId, result.getUserId(), "User ID should be empty string when passed as empty");
        assertNull(result.getErrorMessage(), "Success result should have null error message");
    }

    @Test
    void testFailureWithEmptyErrorMessage() {
        String emptyError = "";
        AuthResult result = AuthResult.failure(emptyError);
        
        assertFalse(result.isSuccess(), "Failure result should have success=false");
        assertNull(result.getUserId(), "Failure result should have null user ID");
        assertEquals(emptyError, result.getErrorMessage(), "Error message should be empty string when passed as empty");
    }
}
