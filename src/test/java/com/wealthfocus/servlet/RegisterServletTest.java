package com.wealthfocus.servlet;

import com.wealthfocus.service.AuthService;
import com.wealthfocus.service.RegisterResult;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegisterServlet.
 * Tests registration form display and submission handling with validation.
 * 
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 9.2, 10.2, 10.4
 */
class RegisterServletTest {

    private RegisterServlet servlet;
    private AuthService mockAuthService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() {
        mockAuthService = Mockito.mock(AuthService.class);
        servlet = new RegisterServlet(mockAuthService);
        
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
    void testDoGetDisplaysRegistrationForm() throws Exception {
        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).getRequestDispatcher("/WEB-INF/views/register.jsp");
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    // ========== doPost Tests - Validation ==========

    @Test
    void testDoPostWithEmptyName() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("");
        when(mockRequest.getParameter("email")).thenReturn("test@example.com");
        when(mockRequest.getParameter("password")).thenReturn("password123");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("password123");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("name");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    void testDoPostWithEmptyEmail() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("John Doe");
        when(mockRequest.getParameter("email")).thenReturn("");
        when(mockRequest.getParameter("password")).thenReturn("password123");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("password123");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    void testDoPostWithInvalidEmailFormat() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("John Doe");
        when(mockRequest.getParameter("email")).thenReturn("invalid-email");
        when(mockRequest.getParameter("password")).thenReturn("password123");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("password123");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("email") && 
                   errorMap.get("email").contains("Invalid email format");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    void testDoPostWithEmptyPassword() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("John Doe");
        when(mockRequest.getParameter("email")).thenReturn("test@example.com");
        when(mockRequest.getParameter("password")).thenReturn("");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("password123");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    void testDoPostWithPasswordMismatch() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("John Doe");
        when(mockRequest.getParameter("email")).thenReturn("test@example.com");
        when(mockRequest.getParameter("password")).thenReturn("password123");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("different456");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("confirmPassword") && 
                   errorMap.get("confirmPassword").contains("do not match");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    void testDoPostPreservesInputOnValidationError() throws Exception {
        // Arrange
        String name = "John Doe";
        String email = "test@example.com";
        when(mockRequest.getParameter("name")).thenReturn(name);
        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn("");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert - verify input is preserved (except passwords)
        verify(mockRequest).setAttribute("name", name);
        verify(mockRequest).setAttribute("email", email);
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    // ========== doPost Tests - Successful Registration ==========

    @Test
    void testDoPostSuccessfulRegistration() throws Exception {
        // Arrange
        String name = "John Doe";
        String email = "test@example.com";
        String password = "password123";
        String userId = "user-123";

        when(mockRequest.getParameter("name")).thenReturn(name);
        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);
        when(mockRequest.getParameter("confirmPassword")).thenReturn(password);

        RegisterResult successResult = RegisterResult.success(userId);
        when(mockAuthService.register(name, email, password)).thenReturn(successResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockAuthService).register(name, email, password);
        verify(mockSession).setAttribute("userId", userId);
        verify(mockResponse).sendRedirect("/dashboard");
    }

    // ========== doPost Tests - Registration Failure ==========

    @Test
    void testDoPostRegistrationFailureDuplicateEmail() throws Exception {
        // Arrange
        String name = "John Doe";
        String email = "existing@example.com";
        String password = "password123";

        when(mockRequest.getParameter("name")).thenReturn(name);
        when(mockRequest.getParameter("email")).thenReturn(email);
        when(mockRequest.getParameter("password")).thenReturn(password);
        when(mockRequest.getParameter("confirmPassword")).thenReturn(password);

        Map<String, String> errors = new HashMap<>();
        errors.put("email", "An account with this email already exists");
        RegisterResult failureResult = RegisterResult.failure(errors);
        when(mockAuthService.register(name, email, password)).thenReturn(failureResult);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockAuthService).register(name, email, password);
        verify(mockRequest).setAttribute("name", name);
        verify(mockRequest).setAttribute("email", email);
        verify(mockRequest).setAttribute("errors", errors);
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockSession, never()).setAttribute(anyString(), anyString());
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testDoPostWithMultipleValidationErrors() throws Exception {
        // Arrange
        when(mockRequest.getParameter("name")).thenReturn("");
        when(mockRequest.getParameter("email")).thenReturn("invalid-email");
        when(mockRequest.getParameter("password")).thenReturn("");
        when(mockRequest.getParameter("confirmPassword")).thenReturn("different");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequest).setAttribute(eq("errors"), argThat(errors -> {
            @SuppressWarnings("unchecked")
            Map<String, String> errorMap = (Map<String, String>) errors;
            return errorMap.containsKey("name") && 
                   errorMap.containsKey("email") && 
                   errorMap.containsKey("password");
        }));
        verify(mockDispatcher).forward(mockRequest, mockResponse);
        verify(mockAuthService, never()).register(anyString(), anyString(), anyString());
    }
}
