package com.wealthfocus.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the outcome of registration operations.
 * Unlike AuthResult which has a single error message, RegisterResult can have multiple 
 * field-specific validation errors (e.g., "email" -> "Invalid email format", 
 * "password" -> "Passwords don't match").
 * 
 * Requirements: 1.1, 9.2
 */
public class RegisterResult {
    private final boolean success;
    private final String userId;
    private final Map<String, String> errors;

    private RegisterResult(boolean success, String userId, Map<String, String> errors) {
        this.success = success;
        this.userId = userId;
        this.errors = errors != null ? Collections.unmodifiableMap(new HashMap<>(errors)) : Collections.emptyMap();
    }

    /**
     * Creates a successful registration result with the newly created user's ID.
     * 
     * @param userId the ID of the newly registered user
     * @return a RegisterResult indicating success
     */
    public static RegisterResult success(String userId) {
        return new RegisterResult(true, userId, null);
    }

    /**
     * Creates a failed registration result with field-specific validation errors.
     * 
     * @param errors a map of field names to error messages (e.g., "email" -> "Invalid email format")
     * @return a RegisterResult indicating failure with validation errors
     */
    public static RegisterResult failure(Map<String, String> errors) {
        return new RegisterResult(false, null, errors);
    }

    /**
     * @return true if registration was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return the newly registered user's ID if successful, null otherwise
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return an immutable map of field-specific error messages if registration failed, empty map otherwise
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}
