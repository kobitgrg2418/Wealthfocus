package com.wealthfocus.util;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionManager class.
 * Tests session management functionality using mock HttpSession objects.
 * 
 * Requirements: 4.1, 4.2, 6.1, 6.3
 */
@ExtendWith(MockitoExtension.class)
class SessionManagerTest {
    
    @Mock
    private HttpSession mockSession;
    
    // ========== Tests for setUserId() ==========
    
    @Test
    void testSetUserIdStoresUserIdInSession() {
        String userId = "user-123";
        
        SessionManager.setUserId(mockSession, userId);
        
        verify(mockSession).setAttribute("userId", userId);
    }
    
    @Test
    void testSetUserIdWithNullSessionThrowsException() {
        String userId = "user-123";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SessionManager.setUserId(null, userId);
        });
        
        assertEquals("Session cannot be null", exception.getMessage());
    }
    
    @Test
    void testSetUserIdWithNullUserIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SessionManager.setUserId(mockSession, null);
        });
        
        assertEquals("User ID cannot be null", exception.getMessage());
    }
    
    // ========== Tests for getUserId() ==========
    
    @Test
    void testGetUserIdReturnsUserIdWhenPresent() {
        String userId = "user-123";
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        
        Optional<String> result = SessionManager.getUserId(mockSession);
        
        assertTrue(result.isPresent(), "User ID should be present");
        assertEquals(userId, result.get(), "User ID should match the stored value");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testGetUserIdReturnsEmptyWhenNotPresent() {
        when(mockSession.getAttribute("userId")).thenReturn(null);
        
        Optional<String> result = SessionManager.getUserId(mockSession);
        
        assertFalse(result.isPresent(), "User ID should not be present");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testGetUserIdReturnsEmptyWhenAttributeIsNotString() {
        when(mockSession.getAttribute("userId")).thenReturn(12345); // Integer instead of String
        
        Optional<String> result = SessionManager.getUserId(mockSession);
        
        assertFalse(result.isPresent(), "User ID should not be present when attribute is not a String");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testGetUserIdWithNullSessionThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SessionManager.getUserId(null);
        });
        
        assertEquals("Session cannot be null", exception.getMessage());
    }
    
    @Test
    void testGetUserIdWithEmptyString() {
        String userId = "";
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        
        Optional<String> result = SessionManager.getUserId(mockSession);
        
        assertTrue(result.isPresent(), "Empty string should still be returned as present");
        assertEquals("", result.get(), "Empty string should be preserved");
    }
    
    // ========== Tests for isAuthenticated() ==========
    
    @Test
    void testIsAuthenticatedReturnsTrueWhenUserIdPresent() {
        String userId = "user-123";
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        
        boolean result = SessionManager.isAuthenticated(mockSession);
        
        assertTrue(result, "Session should be authenticated when user ID is present");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testIsAuthenticatedReturnsFalseWhenUserIdNotPresent() {
        when(mockSession.getAttribute("userId")).thenReturn(null);
        
        boolean result = SessionManager.isAuthenticated(mockSession);
        
        assertFalse(result, "Session should not be authenticated when user ID is not present");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testIsAuthenticatedReturnsFalseWhenAttributeIsNotString() {
        when(mockSession.getAttribute("userId")).thenReturn(12345); // Integer instead of String
        
        boolean result = SessionManager.isAuthenticated(mockSession);
        
        assertFalse(result, "Session should not be authenticated when attribute is not a String");
        verify(mockSession).getAttribute("userId");
    }
    
    @Test
    void testIsAuthenticatedWithNullSessionThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SessionManager.isAuthenticated(null);
        });
        
        assertEquals("Session cannot be null", exception.getMessage());
    }
    
    // ========== Tests for invalidate() ==========
    
    @Test
    void testInvalidateCallsSessionInvalidate() {
        SessionManager.invalidate(mockSession);
        
        verify(mockSession).invalidate();
    }
    
    @Test
    void testInvalidateWithNullSessionThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SessionManager.invalidate(null);
        });
        
        assertEquals("Session cannot be null", exception.getMessage());
    }
    
    // ========== Integration-style tests ==========
    
    @Test
    void testSetAndGetUserIdWorkTogether() {
        String userId = "user-456";
        
        // Simulate setting and getting
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        
        SessionManager.setUserId(mockSession, userId);
        Optional<String> result = SessionManager.getUserId(mockSession);
        
        assertTrue(result.isPresent(), "User ID should be retrievable after setting");
        assertEquals(userId, result.get(), "Retrieved user ID should match the set value");
    }
    
    @Test
    void testAuthenticationStateAfterSettingUserId() {
        String userId = "user-789";
        
        // Simulate setting user ID and checking authentication
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        
        SessionManager.setUserId(mockSession, userId);
        boolean isAuthenticated = SessionManager.isAuthenticated(mockSession);
        
        assertTrue(isAuthenticated, "Session should be authenticated after setting user ID");
    }
    
    @Test
    void testAuthenticationStateBeforeSettingUserId() {
        when(mockSession.getAttribute("userId")).thenReturn(null);
        
        boolean isAuthenticated = SessionManager.isAuthenticated(mockSession);
        
        assertFalse(isAuthenticated, "Session should not be authenticated before setting user ID");
    }
}
