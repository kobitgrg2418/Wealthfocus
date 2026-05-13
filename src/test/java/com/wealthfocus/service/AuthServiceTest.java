package com.wealthfocus.service;

import com.wealthfocus.dao.UserDAO;
import com.wealthfocus.model.User;
import com.wealthfocus.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService class.
 * Tests registration and authentication functionality with mocked dependencies.
 * 
 * Requirements: 1.1, 1.2, 2.1, 2.3, 3.1, 3.2, 3.3, 8.2, 8.3, 9.1, 9.4
 */
class AuthServiceTest {

    private UserDAO mockUserDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAO.class);
        authService = new AuthService(mockUserDAO);
    }

    // ========== Registration Tests ==========

    @Test
    void testRegisterSuccess() throws SQLException {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        String password = "securePassword123";
        String expectedUserId = "user-123";

        when(mockUserDAO.findByEmail(email)).thenReturn(null); // Email doesn't exist
        when(mockUserDAO.create(any(User.class))).thenReturn(expectedUserId);

        // Act
        RegisterResult result = authService.register(name, email, password);

        // Assert
        assertTrue(result.isSuccess(), "Registration should succeed for new email");
        assertEquals(expectedUserId, result.getUserId(), "Should return the created user ID");
        assertTrue(result.getErrors().isEmpty(), "Should have no errors on success");

        // Verify interactions
        verify(mockUserDAO).findByEmail(email);
        verify(mockUserDAO).create(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() throws SQLException {
        // Arrange
        String name = "John Doe";
        String email = "existing@example.com";
        String password = "securePassword123";

        User existingUser = new User();
        existingUser.setId("existing-user-id");
        existingUser.setEmail(email);

        when(mockUserDAO.findByEmail(email)).thenReturn(existingUser); // Email already exists

        // Act
        RegisterResult result = authService.register(name, email, password);

        // Assert
        assertFalse(result.isSuccess(), "Registration should fail for duplicate email");
        assertNull(result.getUserId(), "Should not return user ID on failure");
        assertTrue(result.getErrors().containsKey("email"), "Should have email error");
        assertEquals("An account with this email already exists", result.getErrors().get("email"));

        // Verify that create was never called
        verify(mockUserDAO).findByEmail(email);
        verify(mockUserDAO, never()).create(any(User.class));
    }

    @Test
    void testRegisterPasswordIsHashed() throws SQLException {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        String password = "plainTextPassword";

        when(mockUserDAO.findByEmail(email)).thenReturn(null);
        when(mockUserDAO.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Verify that the password hash is not the plain text password
            assertNotEquals(password, user.getPasswordHash(), 
                "Password should be hashed, not stored as plain text");
            // Verify that the hash starts with BCrypt prefix
            assertTrue(user.getPasswordHash().startsWith("$2a$") || 
                      user.getPasswordHash().startsWith("$2b$") || 
                      user.getPasswordHash().startsWith("$2y$"),
                "Password hash should be a BCrypt hash");
            return "user-123";
        });

        // Act
        RegisterResult result = authService.register(name, email, password);

        // Assert
        assertTrue(result.isSuccess(), "Registration should succeed");
        verify(mockUserDAO).create(any(User.class));
    }

    @Test
    void testRegisterDatabaseError() throws SQLException {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        String password = "securePassword123";

        when(mockUserDAO.findByEmail(email)).thenThrow(new SQLException("Database connection failed"));

        // Act
        RegisterResult result = authService.register(name, email, password);

        // Assert
        assertFalse(result.isSuccess(), "Registration should fail on database error");
        assertNull(result.getUserId(), "Should not return user ID on error");
        assertTrue(result.getErrors().containsKey("general"), "Should have general error");
        assertTrue(result.getErrors().get("general").contains("error occurred"), 
            "Error message should be user-friendly");
        assertFalse(result.getErrors().get("general").contains("SQLException"), 
            "Error message should not expose system details");
    }

    // ========== Authentication Tests ==========

    @Test
    void testAuthenticateSuccess() throws SQLException {
        // Arrange
        String email = "john@example.com";
        String password = "correctPassword";
        String userId = "user-123";

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(password)); // Hash the password

        when(mockUserDAO.findByEmail(email)).thenReturn(user);

        // Act
        AuthResult result = authService.authenticate(email, password);

        // Assert
        assertTrue(result.isSuccess(), "Authentication should succeed with correct credentials");
        assertEquals(userId, result.getUserId(), "Should return the user ID");
        assertNull(result.getErrorMessage(), "Should have no error message on success");

        verify(mockUserDAO).findByEmail(email);
    }

    @Test
    void testAuthenticateNonExistentEmail() throws SQLException {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "anyPassword";

        when(mockUserDAO.findByEmail(email)).thenReturn(null); // User doesn't exist

        // Act
        AuthResult result = authService.authenticate(email, password);

        // Assert
        assertFalse(result.isSuccess(), "Authentication should fail for non-existent email");
        assertNull(result.getUserId(), "Should not return user ID on failure");
        assertEquals("Invalid email or password", result.getErrorMessage(), 
            "Should return generic error message");

        verify(mockUserDAO).findByEmail(email);
    }

    @Test
    void testAuthenticateIncorrectPassword() throws SQLException {
        // Arrange
        String email = "john@example.com";
        String correctPassword = "correctPassword";
        String incorrectPassword = "wrongPassword";
        String userId = "user-123";

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(correctPassword)); // Hash the correct password

        when(mockUserDAO.findByEmail(email)).thenReturn(user);

        // Act
        AuthResult result = authService.authenticate(email, incorrectPassword);

        // Assert
        assertFalse(result.isSuccess(), "Authentication should fail with incorrect password");
        assertNull(result.getUserId(), "Should not return user ID on failure");
        assertEquals("Invalid email or password", result.getErrorMessage(), 
            "Should return generic error message");

        verify(mockUserDAO).findByEmail(email);
    }

    @Test
    void testAuthenticateErrorMessagesAreGeneric() throws SQLException {
        // This test verifies Requirement 9.4: error messages should not reveal whether email exists
        
        String email = "test@example.com";
        String password = "password";

        // Test 1: Non-existent email
        when(mockUserDAO.findByEmail(email)).thenReturn(null);
        AuthResult result1 = authService.authenticate(email, password);
        String errorForNonExistentEmail = result1.getErrorMessage();

        // Test 2: Incorrect password
        User user = new User();
        user.setId("user-123");
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash("differentPassword"));
        when(mockUserDAO.findByEmail(email)).thenReturn(user);
        AuthResult result2 = authService.authenticate(email, password);
        String errorForIncorrectPassword = result2.getErrorMessage();

        // Assert: Both error messages should be identical
        assertEquals(errorForNonExistentEmail, errorForIncorrectPassword,
            "Error messages for non-existent email and incorrect password should be identical " +
            "to prevent email enumeration attacks (Requirement 9.4)");
        assertEquals("Invalid email or password", errorForNonExistentEmail,
            "Error message should be generic");
    }

    @Test
    void testAuthenticateDatabaseError() throws SQLException {
        // Arrange
        String email = "john@example.com";
        String password = "password";

        when(mockUserDAO.findByEmail(email)).thenThrow(new SQLException("Database connection failed"));

        // Act
        AuthResult result = authService.authenticate(email, password);

        // Assert
        assertFalse(result.isSuccess(), "Authentication should fail on database error");
        assertNull(result.getUserId(), "Should not return user ID on error");
        assertTrue(result.getErrorMessage().contains("error occurred"), 
            "Error message should be user-friendly");
        assertFalse(result.getErrorMessage().contains("SQLException"), 
            "Error message should not expose system details");
    }

    @Test
    void testAuthenticateWithEmptyPassword() throws SQLException {
        // Arrange
        String email = "john@example.com";
        String emptyPassword = "";
        String userId = "user-123";

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash("actualPassword"));

        when(mockUserDAO.findByEmail(email)).thenReturn(user);

        // Act
        AuthResult result = authService.authenticate(email, emptyPassword);

        // Assert
        assertFalse(result.isSuccess(), "Authentication should fail with empty password");
        assertEquals("Invalid email or password", result.getErrorMessage());
    }

    @Test
    void testRegisterCreatesUserWithCorrectData() throws SQLException {
        // Arrange
        String name = "Jane Smith";
        String email = "jane@example.com";
        String password = "securePass456";

        when(mockUserDAO.findByEmail(email)).thenReturn(null);
        when(mockUserDAO.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Verify user data
            assertEquals(name, user.getName(), "User name should match");
            assertEquals(email, user.getEmail(), "User email should match");
            assertNotNull(user.getPasswordHash(), "Password hash should not be null");
            assertNotEquals(password, user.getPasswordHash(), "Password should be hashed");
            return "new-user-id";
        });

        // Act
        RegisterResult result = authService.register(name, email, password);

        // Assert
        assertTrue(result.isSuccess());
        verify(mockUserDAO).create(any(User.class));
    }
}
