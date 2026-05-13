package com.wealthfocus.dao;

import com.wealthfocus.model.User;
import net.jqwik.api.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for UserDAO.
 * These tests validate universal properties that should hold for all valid inputs.
 */
class UserDAOPropertyTest {

    private UserDAO userDAO;
    private List<String> createdUserIds;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        createdUserIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up created users after each test
        for (String userId : createdUserIds) {
            deleteUser(userId);
        }
        createdUserIds.clear();
    }

    /**
     * Property 1: Registration Creates User Account
     * 
     * **Validates: Requirements 1.1**
     * 
     * For any valid user data (name, email, password), when registration is performed,
     * the system SHALL create a user account that can be retrieved from the database
     * with matching data.
     */
    @Property
    @Label("Property 1: Registration Creates User Account")
    void registrationCreatesUserAccount(
            @ForAll("validUserName") String name,
            @ForAll("validEmail") String email,
            @ForAll("validPasswordHash") String passwordHash) throws SQLException {
        
        // Arrange: Create a user with random valid data
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        // Act: Create the user in the database
        String userId = userDAO.create(user);
        createdUserIds.add(userId);

        // Assert: User can be retrieved and data matches
        assertNotNull(userId, "User ID should not be null");
        assertFalse(userId.isEmpty(), "User ID should not be empty");

        // Retrieve by ID
        User retrievedById = userDAO.findById(userId);
        assertNotNull(retrievedById, "User should be retrievable by ID");
        assertEquals(userId, retrievedById.getId(), "Retrieved user ID should match");
        assertEquals(name, retrievedById.getName(), "Retrieved user name should match");
        assertEquals(email, retrievedById.getEmail(), "Retrieved user email should match");
        assertEquals(passwordHash, retrievedById.getPasswordHash(), "Retrieved password hash should match");

        // Retrieve by email
        User retrievedByEmail = userDAO.findByEmail(email);
        assertNotNull(retrievedByEmail, "User should be retrievable by email");
        assertEquals(userId, retrievedByEmail.getId(), "Retrieved user ID should match when found by email");
        assertEquals(name, retrievedByEmail.getName(), "Retrieved user name should match when found by email");
        assertEquals(email, retrievedByEmail.getEmail(), "Retrieved user email should match when found by email");
        assertEquals(passwordHash, retrievedByEmail.getPasswordHash(), "Retrieved password hash should match when found by email");
    }

    /**
     * Property 2: Duplicate Email Rejection
     * 
     * **Validates: Requirements 1.2, 8.2**
     * 
     * For any email address, if a user is already registered with that email,
     * then attempting to register another user with the same email SHALL fail
     * with an appropriate error (SQLException due to unique constraint).
     */
    @Property
    @Label("Property 2: Duplicate Email Rejection")
    void duplicateEmailRejection(
            @ForAll("validUserName") String name1,
            @ForAll("validUserName") String name2,
            @ForAll("validEmail") String email,
            @ForAll("validPasswordHash") String passwordHash1,
            @ForAll("validPasswordHash") String passwordHash2) throws SQLException {
        
        // Arrange: Create first user with a specific email
        User user1 = new User();
        user1.setName(name1);
        user1.setEmail(email);
        user1.setPasswordHash(passwordHash1);

        // Act: Create the first user successfully
        String userId1 = userDAO.create(user1);
        createdUserIds.add(userId1);
        assertNotNull(userId1, "First user should be created successfully");

        // Arrange: Create second user with the SAME email but different data
        User user2 = new User();
        user2.setName(name2);
        user2.setEmail(email); // Same email as user1
        user2.setPasswordHash(passwordHash2);

        // Act & Assert: Attempting to create second user with duplicate email should throw SQLException
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.create(user2);
        }, "Creating a user with duplicate email should throw SQLException");

        // Verify the exception is related to duplicate key/unique constraint
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(
            errorMessage.contains("duplicate") || 
            errorMessage.contains("unique") || 
            errorMessage.contains("constraint"),
            "SQLException should indicate duplicate key or unique constraint violation. Got: " + exception.getMessage()
        );

        // Verify only the first user exists in the database
        User retrievedUser = userDAO.findByEmail(email);
        assertNotNull(retrievedUser, "First user should still exist");
        assertEquals(userId1, retrievedUser.getId(), "Retrieved user should be the first user");
        assertEquals(name1, retrievedUser.getName(), "Retrieved user should have first user's name");
        assertEquals(passwordHash1, retrievedUser.getPasswordHash(), "Retrieved user should have first user's password hash");
    }

    // ========== Generators ==========

    /**
     * Generates valid user names.
     * Names should be non-empty strings with reasonable length.
     */
    @Provide
    Arbitrary<String> validUserName() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(100)
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
    }

    /**
     * Generates valid email addresses.
     * Format: localpart@domain.tld
     * Includes a timestamp component to ensure uniqueness across test runs.
     */
    @Provide
    Arbitrary<String> validEmail() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(3)
                .ofMaxLength(20);

        Arbitrary<String> domain = Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(3)
                .ofMaxLength(20);

        Arbitrary<String> tld = Arbitraries.of("com", "org", "net", "edu", "gov", "io", "co");

        // Add timestamp to ensure uniqueness across test runs
        return Combinators.combine(localPart, domain, tld)
                .as((local, dom, t) -> local + System.nanoTime() + "@" + dom + "." + t);
    }

    /**
     * Generates valid password hashes.
     * In reality, these would be BCrypt hashes, but for testing we use random strings
     * that simulate hash format.
     */
    @Provide
    Arbitrary<String> validPasswordHash() {
        // BCrypt hashes are 60 characters long and start with $2a$, $2b$, or $2y$
        // For testing purposes, we'll generate realistic-looking hashes
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars("./")
                .ofLength(53)
                .map(s -> "$2a$12$" + s);
    }

    // ========== Helper Methods ==========

    /**
     * Deletes a user from the database by ID.
     * Used for test cleanup.
     */
    private void deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = com.wealthfocus.util.DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }
}
