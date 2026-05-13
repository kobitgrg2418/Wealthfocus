package com.wealthfocus.util;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Utility class for managing user session state in the authentication system.
 * 
 * This class provides a centralized way to store and retrieve user authentication
 * information from HTTP sessions. It encapsulates the session attribute name and
 * provides type-safe methods for session operations.
 */
public class SessionManager {
    
    /**
     * Session attribute key for storing the authenticated user's ID.
     */
    private static final String USER_ID_ATTRIBUTE = "userId";
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SessionManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Stores the user ID in the session to mark the user as authenticated.
     * 
     * This method should be called after successful login or registration to
     * establish an authenticated session for the user.
     * 
     * @param session the HTTP session to store the user ID in
     * @param userId the ID of the authenticated user
     * @throws IllegalArgumentException if session or userId is null
     */
    public static void setUserId(HttpSession session, String userId) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        session.setAttribute(USER_ID_ATTRIBUTE, userId);
    }
    
    /**
     * Retrieves the user ID from the session.
     * 
     * This method returns an Optional to handle the case where no user is
     * authenticated (i.e., the session does not contain a user ID).
     * 
     * @param session the HTTP session to retrieve the user ID from
     * @return an Optional containing the user ID if present, or empty if not authenticated
     * @throws IllegalArgumentException if session is null
     */
    public static Optional<String> getUserId(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        Object userId = session.getAttribute(USER_ID_ATTRIBUTE);
        
        if (userId instanceof String) {
            return Optional.of((String) userId);
        }
        
        return Optional.empty();
    }
    
    /**
     * Checks if the current session represents an authenticated user.
     * 
     * A session is considered authenticated if it contains a valid user ID.
     * 
     * @param session the HTTP session to check
     * @return true if the session contains a user ID, false otherwise
     * @throws IllegalArgumentException if session is null
     */
    public static boolean isAuthenticated(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        return getUserId(session).isPresent();
    }
    
    /**
     * Invalidates the session, removing all session data including authentication state.
     * 
     * This method should be called during logout to ensure the user is no longer
     * authenticated and all session data is cleared.
     * 
     * @param session the HTTP session to invalidate
     * @throws IllegalArgumentException if session is null
     */
    public static void invalidate(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        session.invalidate();
    }
}
