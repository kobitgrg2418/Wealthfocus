package com.wealthfocus.service;

import com.wealthfocus.dao.UserDAO;
import com.wealthfocus.model.User;
import com.wealthfocus.util.PasswordUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core authentication service that orchestrates user registration and login.
 * 
 * This service validates inputs, hashes passwords, interacts with the database via UserDAO,
 * and returns appropriate result objects. For authentication errors, it uses generic messages
 * that don't reveal whether an email exists (Requirement 9.4).
 * 
 * Requirements: 1.1, 1.2, 2.1, 2.3, 3.1, 3.2, 3.3, 8.2, 8.3, 9.1, 9.4
 */
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO;
    
    /**
     * Creates an AuthService with a new UserDAO instance.
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Creates an AuthService with a provided UserDAO instance.
     * This constructor is useful for testing with mock DAOs.
     * 
     * @param userDAO the UserDAO instance to use for database operations
     */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    /**
     * Registers a new user with the provided credentials.
     * 
     * This method:
     * 1. Checks if the email already exists in the database
     * 2. Hashes the password using BCrypt
     * 3. Creates the user in the database
     * 4. Returns a RegisterResult with the new user ID or validation errors
     * 
     * @param name the user's full name
     * @param email the user's email address (must be unique)
     * @param password the user's plain text password (will be hashed)
     * @return RegisterResult indicating success with user ID or failure with error messages
     */
    public RegisterResult register(String name, String email, String password) {
        Map<String, String> errors = new HashMap<>();
        
        try {
            // Check if email already exists (Requirements 1.2, 8.2)
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                errors.put("email", "An account with this email already exists");
                return RegisterResult.failure(errors);
            }
            
            // Hash the password (Requirement 2.1)
            String passwordHash = PasswordUtil.hash(password);
            
            // Create the user object
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(passwordHash);
            
            // Save to database (Requirement 1.1)
            String userId = userDAO.create(newUser);
            
            return RegisterResult.success(userId);
            
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Registration DB error", e);
            errors.put("general", "An error occurred during registration. Please try again later.");
            return RegisterResult.failure(errors);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Registration error", e);
            errors.put("general", "An unexpected error occurred. Please try again later.");
            return RegisterResult.failure(errors);
        }
    }
    
    /**
     * Authenticates a user with the provided credentials.
     * 
     * This method:
     * 1. Looks up the user by email
     * 2. Verifies the password against the stored hash using BCrypt
     * 3. Returns an AuthResult with the user ID or a generic error message
     * 
     * For security (Requirement 9.4), this method returns the same generic error message
     * whether the email doesn't exist or the password is incorrect. This prevents
     * attackers from determining which emails are registered in the system.
     * 
     * @param email the user's email address
     * @param password the user's plain text password
     * @return AuthResult indicating success with user ID or failure with error message
     */
    public AuthResult authenticate(String email, String password) {
        try {
            // Find user by email (Requirements 3.2, 8.3)
            User user = userDAO.findByEmail(email);
            
            if (user == null) {
                return AuthResult.failure("Invalid email or password");
            }

            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                return AuthResult.failure("Invalid email or password");
            }

            boolean passwordValid = PasswordUtil.verify(password, user.getPasswordHash());
            
            if (!passwordValid) {
                // Return same generic error message (Requirement 9.4)
                return AuthResult.failure("Invalid email or password");
            }
            
            // Authentication successful (Requirement 3.1)
            return AuthResult.success(user.getId());
            
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Authentication DB error", e);
            return AuthResult.failure("An error occurred during authentication. Please try again later.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Authentication error", e);
            return AuthResult.failure("An unexpected error occurred. Please try again later.");
        }
    }
}
