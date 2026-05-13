package com.wealthfocus.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Unit tests for LogoutServlet.
 * Tests logout functionality including session invalidation and redirect.
 * 
 * Requirements: 6.1, 6.2, 6.3
 */
class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        servlet = new LogoutServlet();
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockSession = Mockito.mock(HttpSession.class);
        
        when(mockRequest.getContextPath()).thenReturn("");
    }

    // ========== doPost Tests ==========

    @Test
    void testDoPostInvalidatesSessionAndRedirects() throws Exception {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirements 6.1, 6.2, 6.3
        verify(mockRequest).getSession(false);
        verify(mockSession).invalidate();
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testDoPostWithNullSessionRedirects() throws Exception {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(null);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 6.2
        verify(mockRequest).getSession(false);
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testDoPostWithContextPathRedirects() throws Exception {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockRequest.getContextPath()).thenReturn("/wealthfocus");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirements 6.1, 6.2
        verify(mockSession).invalidate();
        verify(mockResponse).sendRedirect("/wealthfocus/login");
    }

    @Test
    void testDoPostDoesNotCreateNewSession() throws Exception {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(null);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - verify getSession(false) is called, not getSession() or getSession(true)
        verify(mockRequest).getSession(false);
        verify(mockRequest, never()).getSession();
        verify(mockRequest, never()).getSession(true);
    }
}
