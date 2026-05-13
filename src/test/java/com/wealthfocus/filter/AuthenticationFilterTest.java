package com.wealthfocus.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationFilter.
 * Tests route protection, authentication checking, and URL preservation.
 * 
 * Requirements: 5.1, 5.2, 5.3, 5.4
 */
class AuthenticationFilterTest {

    private AuthenticationFilter filter;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private FilterChain mockChain;
    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationFilter();
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockChain = Mockito.mock(FilterChain.class);
        mockSession = Mockito.mock(HttpSession.class);
        
        // Default setup
        when(mockRequest.getContextPath()).thenReturn("");
    }

    // ========== Public Route Tests ==========

    @Test
    void testLoginRouteIsPublic() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/login");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.3
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testRegisterRouteIsPublic() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/register");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.3
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testStaticResourcesArePublic() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/static/css/main.css");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.3
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testStaticJsResourcesArePublic() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/static/js/app.js");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.3
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testStaticNestedResourcesArePublic() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/static/images/logo.png");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.3
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    // ========== Protected Route Tests - Authenticated User ==========

    @Test
    void testAuthenticatedUserCanAccessProtectedRoute() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("user-123");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.2
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testAuthenticatedUserCanAccessExpensesRoute() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/expenses");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("user-456");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.2
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    // ========== Protected Route Tests - Unauthenticated User ==========

    @Test
    void testUnauthenticatedUserRedirectedToLogin() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.1
        verify(mockResponse).sendRedirect("/login");
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testUnauthenticatedUserWithNoSessionRedirectedToLogin() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/expenses");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.1
        verify(mockResponse).sendRedirect("/login");
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testUserWithSessionButNoUserIdRedirectedToLogin() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.1
        verify(mockResponse).sendRedirect("/login");
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
    }

    // ========== Original URL Preservation Tests ==========

    @Test
    void testOriginalUrlPreservedForGetRequest() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getQueryString()).thenReturn(null);

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.4
        verify(mockSession).setAttribute("originalUrl", "/dashboard");
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testOriginalUrlWithQueryStringPreserved() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/expenses");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getQueryString()).thenReturn("category=food&month=2024-01");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Requirement 5.4
        verify(mockSession).setAttribute("originalUrl", "/expenses?category=food&month=2024-01");
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testOriginalUrlNotPreservedForPostRequest() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("POST");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - POST requests should not preserve URL
        verify(mockSession, never()).setAttribute(eq("originalUrl"), anyString());
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testOriginalUrlNotPreservedForPutRequest() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/expenses");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("PUT");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - PUT requests should not preserve URL
        verify(mockSession, never()).setAttribute(eq("originalUrl"), anyString());
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testOriginalUrlNotPreservedForDeleteRequest() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/expenses");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("DELETE");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - DELETE requests should not preserve URL
        verify(mockSession, never()).setAttribute(eq("originalUrl"), anyString());
        verify(mockResponse).sendRedirect("/login");
    }

    // ========== Context Path Tests ==========

    @Test
    void testFilterWorksWithContextPath() throws Exception {
        // Arrange
        when(mockRequest.getContextPath()).thenReturn("/wealthfocus");
        when(mockRequest.getRequestURI()).thenReturn("/wealthfocus/dashboard");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("user-123");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testPublicRouteWithContextPath() throws Exception {
        // Arrange
        when(mockRequest.getContextPath()).thenReturn("/wealthfocus");
        when(mockRequest.getRequestURI()).thenReturn("/wealthfocus/login");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert
        verify(mockChain).doFilter(mockRequest, mockResponse);
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testRedirectIncludesContextPath() throws Exception {
        // Arrange
        when(mockRequest.getContextPath()).thenReturn("/wealthfocus");
        when(mockRequest.getRequestURI()).thenReturn("/wealthfocus/dashboard");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert
        verify(mockResponse).sendRedirect("/wealthfocus/login");
    }

    // ========== Edge Cases ==========

    @Test
    void testRootPathIsProtected() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert
        verify(mockResponse).sendRedirect("/login");
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testEmptyQueryStringHandled() throws Exception {
        // Arrange
        when(mockRequest.getRequestURI()).thenReturn("/dashboard");
        when(mockRequest.getSession(false)).thenReturn(null);
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getQueryString()).thenReturn("");

        // Act
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert - Empty query string should not be appended
        verify(mockSession).setAttribute("originalUrl", "/dashboard");
        verify(mockResponse).sendRedirect("/login");
    }
}
