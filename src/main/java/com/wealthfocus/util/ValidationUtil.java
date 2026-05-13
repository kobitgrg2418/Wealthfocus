package com.wealthfocus.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation in registration and login forms.
 * 
 * This class provides methods to validate email format, check for non-empty values,
 * and verify password matching. All methods handle null values gracefully.
 */
public class ValidationUtil {
    
    /**
     * Regular expression pattern for validating email addresses.
     * 
     * This pattern validates:
     * - Local part: alphanumeric characters, dots, hyphens, underscores
     * - @ symbol
     * - Domain: alphanumeric characters, dots, hyphens
     * - TLD: at least 2 characters
     * 
     * Note: This is a reasonable regex pattern that covers most common email formats.
     * It does not implement the full RFC 5322 specification, which is extremely complex.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates whether a string is a valid email address format.
     * 
     * This method uses a regex pattern to check if the email conforms to a
     * reasonable email format. It handles null values gracefully by returning false.
     * 
     * @param email the email address to validate
     * @return true if the email is valid, false if null, empty, or invalid format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Checks whether a string value is non-empty.
     * 
     * A value is considered non-empty if it is not null and contains at least
     * one non-whitespace character after trimming.
     * 
     * @param value the string value to check
     * @return true if the value is non-empty, false if null, empty, or only whitespace
     */
    public static boolean isNonEmpty(String value) {
        if (value == null) {
            return false;
        }
        
        return !value.trim().isEmpty();
    }
    
    /**
     * Verifies that two password strings match exactly.
     * 
     * This method is typically used to validate password confirmation during
     * registration. It handles null values gracefully by returning false if
     * either password is null.
     * 
     * @param password the original password
     * @param confirmPassword the confirmation password to compare
     * @return true if both passwords are non-null and match exactly, false otherwise
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        
        return password.equals(confirmPassword);
    }
}
