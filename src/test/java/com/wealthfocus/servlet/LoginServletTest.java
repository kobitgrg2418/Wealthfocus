package com.wealthfocus.servlet;

import com.wealthfocus.service.AuthService;
import com.wealthfocus.service.AuthResult;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginServlet.
 * Tests login form display and submission handling with validation.
 * 
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 5.4, 9.1, 9.4, 10.1, 10.4
 */
class LoginServletTest {

    private LoginServlet servlet;
    private AuthService mockAuthService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() {
        mockAuthService = Mockito.mock(AuthService.class);
        servlet = new LoginServlet(mockAuthService);
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockSession = Mockito.mock(HttpSession.class);
        mockDispatcher = Mockito.mock(RequestDispatcher.class);
        
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString())).thenReturn(mockDispatcher);
        when(mockRequest.getContextPath()).thenReturn("");
    }

    // ========== doGet Tests ==========

    @Test
    void testDoGetDisplaysLoginForm() throws Exception {
        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Requirement 10.1
        verify(mockRequest).getRequestDispatcher("/WEB-INF/views/login.jsp");
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    // ========== doPost Tests - Validation ==========

    @Test
    void testDoPostWithEmptyEmail() throws Exception {
        // Arrange
        when(mockRequest.getParameter("email")).thenReturn("");
        when(mockRequest.getParameter("password")).thenReturn("password123");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 3.4
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email") && 
                   errorMap.get("email").equals("Email is required");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testDoPostWithEmptyPassword() throws Exception {
        // Arrange
        when(mockRequest.getParameter("email")).thenReturn("test@example.com");
        when(mockRequest.getParameter("password")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 3.4
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("password") && 
                   errorMap.get("password").equals("Password is required");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testDoPostWithBothFieldsEmpty() throws Exception {
        // Arrange
        when(mockRequest.getParameter("email")).thenReturn("");
        when(mockRequest.getParameter("password")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 3.4
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email") && errorMap.containsKey("password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testDoPostPreservesEmailOnValidationError() throws Exception {
        // Arrange
        String email = "test@example.com";
        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 10.4
        verify(mockRequest).setAttribute("email", email);
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    // ========== doPost Tests - Successful Login ==========

    @Test
    void testDoPostSuccessfulLoginRedirectsToDashboard() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String userId = "user-123";

        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);

        AuthResult successResult = AuthResult.success(userId);
        when(mockAuthService.authenticate(email, password)).thenReturn(successResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirements 3.1, 3.5
        verify(mockAuthService).authenticate(email, password);
        verify(mockSession).setAttribute("userId", userId);
        verify(mockResponse).sendRedirect("/dashboard");
    }

    @Test
    void testDoPostSuccessfulLoginRedirectsToOriginalUrl() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String userId = "user-123";
        String originalUrl = "/expenses";

        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);
        when(mockSession.getAttribute("originalUrl")).thenReturn(originalUrl);

        AuthResult successResult = AuthResult.success(userId);
        when(mockAuthService.authenticate(email, password)).thenReturn(successResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 5.4
        verify(mockAuthService).authenticate(email, password);
        verify(mockSession).setAttribute("userId", userId);
        verify(mockSession).removeAttribute("originalUrl");
        verify(mockResponse).sendRedirect(originalUrl);
    }

    // ========== doPost Tests - Login Failure ==========

    @Test
    void testDoPostFailedLoginInvalidCredentials() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);

        AuthResult failureResult = AuthResult.failure("Invalid email or password");
        when(mockAuthService.authenticate(email, password)).thenReturn(failureResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirements 3.2, 3.3, 9.1, 9.4
        verify(mockAuthService).authenticate(email, password);
        verify(mockRequest).setAttribute("email", email);
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("general") && 
                   errorMap.get("general").equals("Invalid email or password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockSession, never()).setAttribute(eq("userId"), anyString());
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testDoPostFailedLoginNonExistentEmail() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";

        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);

        AuthResult failureResult = AuthResult.failure("Invalid email or password");
        when(mockAuthService.authenticate(email, password)).thenReturn(failureResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirements 3.2, 9.4
        verify(mockAuthService).authenticate(email, password);
        verify(mockRequest).setAttribute("email", email);
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("general") && 
                   errorMap.get("general").equals("Invalid email or password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockSession, never()).setAttribute(eq("userId"), anyString());
    }

    @Test
    void testDoPostPreservesEmailOnAuthenticationFailure() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);

        AuthResult failureResult = AuthResult.failure("Invalid email or password");
        when(mockAuthService.authenticate(email, password)).thenReturn(failureResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 10.4
        verify(mockRequest).setAttribute("email", email);
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    void testDoPostWithNullParameters() throws Exception {
        // Arrange
        when(mockRequest.getParameter("email")).thenReturn(null);
        when(mockRequest.getParameter("password")).thenReturn(null);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 3.4
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email") && errorMap.containsKey("password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testDoPostWithWhitespaceOnlyFields() throws Exception {
        // Arrange
        when(mockRequest.getParameter("email")).thenReturn("   ");
        when(mockRequest.getParameter("password")).thenReturn("   ");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - Requirement 3.4
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email") && errorMap.containsKey("password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).authenticate(anyString(), anyString());
    }
}
