package com.wealthfocus.util;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for ValidationUtil.
 * These tests validate universal properties that should hold for all valid email inputs.
 */
class ValidationUtilPropertyTest {

    /**
     * Property 4: Invalid Email Format Rejection
     * 
     * **Validates: Requirements 1.4**
     * 
     * For any string that does not conform to valid email format, when used as an email
     * in registration, the system SHALL reject the registration with an error message.
     * 
     * This property verifies that invalid email formats are consistently rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Missing @ symbol")
    void invalidEmailWithoutAtSymbolIsRejected(@ForAll("invalidEmailWithoutAt") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email without @ should be rejected
        assertFalse(result, 
            "Email without @ symbol must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Invalid Email Format Rejection - Missing domain
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that emails without a proper domain are rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Missing domain")
    void invalidEmailWithoutDomainIsRejected(@ForAll("invalidEmailWithoutDomain") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email without domain should be rejected
        assertFalse(result, 
            "Email without domain must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Invalid Email Format Rejection - Missing local part
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that emails without a local part (before @) are rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Missing local part")
    void invalidEmailWithoutLocalPartIsRejected(@ForAll("invalidEmailWithoutLocalPart") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email without local part should be rejected
        assertFalse(result, 
            "Email without local part must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Invalid Email Format Rejection - Invalid TLD
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that emails with invalid top-level domains (less than 2 characters) are rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Invalid TLD")
    void invalidEmailWithInvalidTldIsRejected(@ForAll("invalidEmailWithInvalidTld") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email with invalid TLD should be rejected
        assertFalse(result, 
            "Email with invalid TLD must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Invalid Email Format Rejection - Contains spaces
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that emails containing spaces (except leading/trailing which are trimmed) are rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Contains spaces")
    void invalidEmailWithSpacesIsRejected(@ForAll("invalidEmailWithSpaces") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email with spaces should be rejected
        assertFalse(result, 
            "Email with spaces must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Invalid Email Format Rejection - Invalid characters
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that emails containing invalid special characters are rejected.
     */
    @Property
    @Label("Property 4: Invalid Email Format Rejection - Invalid characters")
    void invalidEmailWithInvalidCharactersIsRejected(@ForAll("invalidEmailWithInvalidChars") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Email with invalid characters should be rejected
        assertFalse(result, 
            "Email with invalid characters must be rejected (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Valid Email Format Acceptance
     * 
     * **Validates: Requirements 1.4**
     * 
     * For any string that conforms to valid email format, the system SHALL accept it.
     * This is the positive case - valid emails should be accepted.
     */
    @Property
    @Label("Property 4: Valid Email Format Acceptance - Valid emails are accepted")
    void validEmailIsAccepted(@ForAll("validEmail") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Valid email should be accepted
        assertTrue(result, 
            "Valid email must be accepted (Requirement 1.4): " + email);
    }

    /**
     * Property 4: Email Validation Consistency
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that email validation is deterministic and consistent.
     * Validating the same email multiple times should always produce the same result.
     */
    @Property
    @Label("Property 4: Email Validation Consistency")
    void emailValidationIsConsistent(@ForAll("anyEmail") String email) {
        // Act: Validate the email multiple times
        boolean result1 = ValidationUtil.isValidEmail(email);
        boolean result2 = ValidationUtil.isValidEmail(email);
        boolean result3 = ValidationUtil.isValidEmail(email);
        
        // Assert: All validation attempts should produce the same result
        assertEquals(result1, result2, 
            "Email validation should be consistent across multiple calls");
        assertEquals(result2, result3, 
            "Email validation should be consistent across multiple calls");
    }

    /**
     * Property 4: Null and Empty Email Rejection
     * 
     * **Validates: Requirements 1.4**
     * 
     * Verifies that null and empty strings are rejected as invalid emails.
     */
    @Property
    @Label("Property 4: Null and Empty Email Rejection")
    void nullAndEmptyEmailsAreRejected(@ForAll("nullOrEmptyEmail") String email) {
        // Act: Validate the email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Assert: Null and empty emails should be rejected
        assertFalse(result, 
            "Null and empty emails must be rejected (Requirement 1.4)");
    }

    // ========== Generators ==========

    /**
     * Generates invalid emails without @ symbol.
     * Examples: "userexample.com", "user.example.com", "username"
     */
    @Provide
    Arbitrary<String> invalidEmailWithoutAt() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars(".-_")
                .ofMinLength(1)
                .ofMaxLength(50)
                .filter(s -> !s.contains("@"));
    }

    /**
     * Generates invalid emails without domain (ends with @).
     * Examples: "user@", "test.user@", "admin@"
     */
    @Provide
    Arbitrary<String> invalidEmailWithoutDomain() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars(".-_")
                .ofMinLength(1)
                .ofMaxLength(30);
        
        return localPart.map(local -> local + "@");
    }

    /**
     * Generates invalid emails without local part (starts with @).
     * Examples: "@example.com", "@domain.org", "@test.co.uk"
     */
    @Provide
    Arbitrary<String> invalidEmailWithoutLocalPart() {
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('0', '9')
                .withChars("-")
                .ofMinLength(1)
                .ofMaxLength(20);
        
        Arbitrary<String> tld = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(2)
                .ofMaxLength(6);
        
        return Combinators.combine(domain, tld)
                .as((d, t) -> "@" + d + "." + t);
    }

    /**
     * Generates invalid emails with invalid TLD (less than 2 characters or missing).
     * Examples: "user@example.c", "user@example.", "user@example"
     */
    @Provide
    Arbitrary<String> invalidEmailWithInvalidTld() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('0', '9')
                .withChars(".-_")
                .ofMinLength(1)
                .ofMaxLength(20);
        
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('0', '9')
                .withChars("-")
                .ofMinLength(1)
                .ofMaxLength(20);
        
        // Generate either no TLD, single char TLD, or just a dot
        Arbitrary<String> invalidTld = Arbitraries.of("", ".", ".c", ".1");
        
        return Combinators.combine(localPart, domain, invalidTld)
                .as((local, dom, tld) -> local + "@" + dom + tld);
    }

    /**
     * Generates invalid emails containing spaces.
     * Examples: "user name@example.com", "user@exam ple.com", "user @example.com"
     */
    @Provide
    Arbitrary<String> invalidEmailWithSpaces() {
        Arbitrary<String> part1 = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(10);
        
        Arbitrary<String> part2 = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(10);
        
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(10);
        
        Arbitrary<String> tld = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(2)
                .ofMaxLength(4);
        
        // Generate emails with spaces in different positions
        return Combinators.combine(part1, part2, domain, tld)
                .as((p1, p2, d, t) -> {
                    int position = (p1.hashCode() & 0x7FFFFFFF) % 3;
                    switch (position) {
                        case 0: return p1 + " " + p2 + "@" + d + "." + t; // space in local part
                        case 1: return p1 + p2 + "@ " + d + "." + t; // space after @
                        default: return p1 + p2 + "@" + d + " ." + t; // space before dot
                    }
                });
    }

    /**
     * Generates invalid emails with invalid special characters.
     * Examples: "user#name@example.com", "user@exam$ple.com", "user!@example.com"
     */
    @Provide
    Arbitrary<String> invalidEmailWithInvalidChars() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(10);
        
        Arbitrary<String> invalidChar = Arbitraries.of("!", "#", "$", "%", "&", "*", "(", ")", "=", "+", "[", "]", "{", "}", "|", "\\", "/", "?", "<", ">");
        
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(10);
        
        Arbitrary<String> tld = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(2)
                .ofMaxLength(4);
        
        // Insert invalid character in local part
        return Combinators.combine(localPart, invalidChar, domain, tld)
                .as((local, invalid, d, t) -> local + invalid + "@" + d + "." + t);
    }

    /**
     * Generates valid email addresses.
     * Examples: "user@example.com", "test.user@domain.co.uk", "admin123@test-domain.org"
     */
    @Provide
    Arbitrary<String> validEmail() {
        // Generate local part (before @)
        Arbitrary<String> localPart = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars(".-_")
                .ofMinLength(1)
                .ofMaxLength(30)
                .filter(s -> !s.startsWith(".") && !s.endsWith(".") && !s.startsWith("-") && !s.endsWith("-"));
        
        // Generate domain (between @ and .)
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('0', '9')
                .withChars("-")
                .ofMinLength(1)
                .ofMaxLength(20)
                .filter(s -> !s.startsWith("-") && !s.endsWith("-"));
        
        // Generate TLD (after final .)
        Arbitrary<String> tld = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(2)
                .ofMaxLength(6);
        
        return Combinators.combine(localPart, domain, tld)
                .as((local, dom, t) -> local + "@" + dom + "." + t);
    }

    /**
     * Generates any email-like string (valid or invalid) for consistency testing.
     */
    @Provide
    Arbitrary<String> anyEmail() {
        return Arbitraries.oneOf(
                validEmail(),
                invalidEmailWithoutAt(),
                invalidEmailWithoutDomain(),
                invalidEmailWithoutLocalPart(),
                invalidEmailWithInvalidTld(),
                invalidEmailWithSpaces(),
                invalidEmailWithInvalidChars()
        );
    }

    /**
     * Generates null or empty strings for edge case testing.
     */
    @Provide
    Arbitrary<String> nullOrEmptyEmail() {
        return Arbitraries.of("", "   ", "\t", "\n", "  \t\n  ");
    }
}
