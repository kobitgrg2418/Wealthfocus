package com.wealthfocus.servlet;

import com.google.gson.Gson;
import com.wealthfocus.dao.ExpenseDAO;
import com.wealthfocus.dao.IncomeDAO;
import com.wealthfocus.model.Recommendation;
import com.wealthfocus.service.FinanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvestmentServlet.
 * Tests that investment advice retrieval uses session-based user identification.
 * 
 * **Validates: Requirements 7.1, 7.2, 7.3**
 */
class InvestmentServletTest {

    private InvestmentServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private StringWriter responseWriter;
    private Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new InvestmentServlet();
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockSession = Mockito.mock(HttpSession.class);
        
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockResponse.getWriter()).thenReturn(writer);
        
        gson = new Gson();
    }

    // ========== Session-Based User ID Tests ==========

    @Test
    void testDoGetWithAuthenticatedUserReturnsAdvice() throws Exception {
        // Arrange
        String userId = "user-123";
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockRequest.getParameter("period")).thenReturn("month");

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Requirement 7.1: Servlet retrieves user ID from session
        verify(mockSession).getAttribute("userId");
        verify(mockResponse).setContentType("application/json;charset=UTF-8");
        
        // Verify response contains JSON array
        String response = responseWriter.toString();
        assertTrue(response.startsWith("["), "Response should be a JSON array");
    }

    @Test
    void testDoGetWithoutSessionRedirectsToLogin() throws Exception {
        // Arrange - No user ID in session
        when(mockSession.getAttribute("userId")).thenReturn(null);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Requirement 7.3: Ensures user is authenticated
        verify(mockSession).getAttribute("userId");
        verify(mockResponse).sendRedirect("/login");
        verify(mockResponse, never()).setContentType(anyString());
    }

    @Test
    void testDoGetUsesSessionUserIdNotHardcodedDefault() throws Exception {
        // Arrange
        String authenticatedUserId = "authenticated-user-456";
        when(mockSession.getAttribute("userId")).thenReturn(authenticatedUserId);
        when(mockRequest.getParameter("period")).thenReturn("year");

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Requirement 7.2: Replaces hardcoded DEFAULT_USER_ID with session-based identification
        verify(mockSession).getAttribute("userId");
        verify(mockResponse).setContentType("application/json;charset=UTF-8");
        
        // Verify the servlet completed successfully (no redirect to login)
        verify(mockResponse, never()).sendRedirect(anyString());
    }

    @Test
    void testDoGetWithDifferentPeriods() throws Exception {
        // Arrange
        String userId = "user-789";
        when(mockSession.getAttribute("userId")).thenReturn(userId);

        // Test with different period parameters
        String[] periods = {"week", "month", "year", null};
        
        for (String period : periods) {
            // Reset mocks for each iteration
            Mockito.reset(mockResponse);
            responseWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(responseWriter);
            when(mockResponse.getWriter()).thenReturn(writer);
            
            when(mockRequest.getParameter("period")).thenReturn(period);

            // Act
            servlet.doGet(mockRequest, mockResponse);

            // Assert - Requirement 7.1: Uses authenticated user's ID for all operations
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            String response = responseWriter.toString();
            assertTrue(response.startsWith("["), 
                "Response should be a JSON array for period: " + period);
        }
    }

    @Test
    void testDoGetHandlesEmptyStringUserId() throws Exception {
        // Arrange - Empty string in session (edge case)
        when(mockSession.getAttribute("userId")).thenReturn("");

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Should redirect to login for invalid user ID
        verify(mockResponse).sendRedirect("/login");
    }

    @Test
    void testDoGetHandlesNonStringUserId() throws Exception {
        // Arrange - Non-string object in session (edge case)
        when(mockSession.getAttribute("userId")).thenReturn(12345);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert - Should redirect to login for invalid user ID type
        verify(mockResponse).sendRedirect("/login");
    }
}
