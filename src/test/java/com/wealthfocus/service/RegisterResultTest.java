package com.wealthfocus.service;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RegisterResult class.
 * Tests the success and failure factory methods and field-specific error handling.
 */
public class RegisterResultTest {

    @Test
    public void testSuccessResult() {
        // Arrange
        String userId = "user123";

        // Act
        RegisterResult result = RegisterResult.success(userId);

        // Assert
        assertTrue(result.isSuccess(), "Result should indicate success");
        assertEquals(userId, result.getUserId(), "User ID should match");
        assertTrue(result.getErrors().isEmpty(), "Errors map should be empty for success");
    }

    @Test
    public void testFailureResultWithSingleError() {
        // Arrange
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email format");

        // Act
        RegisterResult result = RegisterResult.failure(errors);

        // Assert
        assertFalse(result.isSuccess(), "Result should indicate failure");
        assertNull(result.getUserId(), "User ID should be null for failure");
        assertEquals(1, result.getErrors().size(), "Should have one error");
        assertEquals("Invalid email format", result.getErrors().get("email"), "Error message should match");
    }

    @Test
    public void testFailureResultWithMultipleErrors() {
        // Arrange
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email format");
        errors.put("password", "Passwords don't match");
        errors.put("name", "Name is required");

        // Act
        RegisterResult result = RegisterResult.failure(errors);

        // Assert
        assertFalse(result.isSuccess(), "Result should indicate failure");
        assertNull(result.getUserId(), "User ID should be null for failure");
        assertEquals(3, result.getErrors().size(), "Should have three errors");
        assertEquals("Invalid email format", result.getErrors().get("email"));
        assertEquals("Passwords don't match", result.getErrors().get("password"));
        assertEquals("Name is required", result.getErrors().get("name"));
    }

    @Test
    public void testFailureResultWithEmptyErrors() {
        // Arrange
        Map<String, String> errors = new HashMap<>();

        // Act
        RegisterResult result = RegisterResult.failure(errors);

        // Assert
        assertFalse(result.isSuccess(), "Result should indicate failure");
        assertNull(result.getUserId(), "User ID should be null for failure");
        assertTrue(result.getErrors().isEmpty(), "Errors map should be empty");
    }

    @Test
    public void testFailureResultWithNullErrors() {
        // Act
        RegisterResult result = RegisterResult.failure(null);

        // Assert
        assertFalse(result.isSuccess(), "Result should indicate failure");
        assertNull(result.getUserId(), "User ID should be null for failure");
        assertNotNull(result.getErrors(), "Errors map should not be null");
        assertTrue(result.getErrors().isEmpty(), "Errors map should be empty when null is passed");
    }

    @Test
    public void testErrorsMapIsImmutable() {
        // Arrange
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email format");
        RegisterResult result = RegisterResult.failure(errors);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            result.getErrors().put("password", "New error");
        }, "Errors map should be immutable");
    }

    @Test
    public void testErrorsMapIsDefensiveCopy() {
        // Arrange
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email format");
        RegisterResult result = RegisterResult.failure(errors);

        // Act - modify original map
        errors.put("password", "New error");

        // Assert - result should not be affected
        assertEquals(1, result.getErrors().size(), "Result should not be affected by changes to original map");
        assertFalse(result.getErrors().containsKey("password"), "Result should not contain the new error");
    }
}
