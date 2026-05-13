# User DAO Property Test

## Overview

This directory contains property-based tests for the UserDAO class, specifically testing the user creation functionality as specified in task 2.3 of the user-authentication spec.

## Test File

- **UserDAOPropertyTest.java**: Contains Property 1 - Registration Creates User Account

## Property Tested

### Property 1: Registration Creates User Account

**Validates: Requirements 1.1**

For any valid user data (name, email, password hash), when a user is created via UserDAO.create(), the system SHALL:
1. Successfully create the user in the database
2. Return a non-null, non-empty user ID
3. Allow the user to be retrieved by ID with matching data
4. Allow the user to be retrieved by email with matching data

## Test Framework

The tests use:
- **JUnit 5** for test execution
- **jqwik** for property-based testing

## Running the Tests

### Prerequisites

1. **Maven must be installed** - See HOW_TO_RUN.md for installation instructions
2. **MySQL must be running** with the wealthfocus database created
3. **Database schema must be set up** - Run database/005_create_users.sql

### Run the Test

```bash
# Run all tests
mvn test

# Run only the UserDAOPropertyTest
mvn test -Dtest=UserDAOPropertyTest

# Run with verbose output
mvn test -Dtest=UserDAOPropertyTest -X
```

## Test Generators

The test uses smart generators to create valid test data:

1. **validUserName()**: Generates alphabetic names (1-100 characters) with proper capitalization
2. **validEmail()**: Generates valid email addresses with format: localpart@domain.tld
   - Includes timestamp component to ensure uniqueness across test runs
3. **validPasswordHash()**: Generates BCrypt-format password hashes ($2a$12$...)

## Test Cleanup

The test automatically cleans up after itself:
- All created users are tracked in the `createdUserIds` list
- The `@AfterEach` method deletes all created users from the database
- This ensures tests don't interfere with each other

## Expected Behavior

When run successfully, the test will:
1. Generate 100+ random combinations of valid user data (by default, jqwik runs 1000 tries)
2. Create each user in the database
3. Verify the user can be retrieved by both ID and email
4. Verify all user data matches what was created
5. Clean up all test data

## Troubleshooting

### Maven not found
```
'mvn' is not recognized...
```
**Solution**: Install Maven following the instructions in HOW_TO_RUN.md

### MySQL connection error
```
Communications link failure
```
**Solution**: 
- Ensure MySQL is running
- Check credentials in src/main/resources/db.properties

### Table doesn't exist
```
Table 'wealthfocus.users' doesn't exist
```
**Solution**: Run the database migration:
```bash
mysql -u root -p wealthfocus < database/005_create_users.sql
```

### Duplicate email errors
If you see duplicate email errors, this indicates:
- The email uniqueness constraint is working correctly
- Previous test data wasn't cleaned up properly
- You may need to manually clean the users table

## Next Steps

After this test passes, the next tasks in the spec are:
- Task 2.4: Write property test for email uniqueness
- Task 3.1: Create PasswordUtil class
- Task 3.2: Write property test for password hashing

