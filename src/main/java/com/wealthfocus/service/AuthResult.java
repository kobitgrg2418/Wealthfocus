package com.wealthfocus.service;

/**
 * Encapsulates the outcome of authentication operations (login).
 * Provides a clean way to return either success with a user ID or failure with an error message.
 * 
 * Requirements: 1.1, 3.1, 9.1
 */
public class AuthResult {
    private final boolean success;
    private final String userId;
    private final String errorMessage;

    private AuthResult(boolean success, String userId, String errorMessage) {
        this.success = success;
        this.userId = userId;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a successful authentication result with the authenticated user's ID.
     * 
     * @param userId the ID of the authenticated user
     * @return an AuthResult indicating success
     */
    public static AuthResult success(String userId) {
        return new AuthResult(true, userId, null);
    }

    /**
     * Creates a failed authentication result with an error message.
     * 
     * @param errorMessage the error message describing why authentication failed
     * @return an AuthResult indicating failure
     */
    public static AuthResult failure(String errorMessage) {
        return new AuthResult(false, null, errorMessage);
    }

    /**
     * @return true if authentication was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return the authenticated user's ID if successful, null otherwise
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the error message if authentication failed, null otherwise
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
