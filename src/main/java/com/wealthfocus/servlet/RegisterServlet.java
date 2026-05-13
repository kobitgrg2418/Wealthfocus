package com.wealthfocus.servlet;

import com.wealthfocus.service.AuthService;
import com.wealthfocus.service.RegisterResult;
import com.wealthfocus.util.SessionManager;
import com.wealthfocus.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet that handles user registration.
 * 
 * This servlet provides two main functions:
 * - GET: Displays the registration form
 * - POST: Processes registration submissions with validation
 * 
 * On successful registration, the user is automatically logged in (session created)
 * and redirected to the dashboard. On failure, the form is redisplayed with
 * validation errors and preserved input (except passwords).
 * 
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 9.2, 10.2, 10.4
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private final AuthService authService;
    
    /**
     * Creates a RegisterServlet with a new AuthService instance.
     */
    public RegisterServlet() {
        this.authService = new AuthService();
    }
    
    /**
     * Creates a RegisterServlet with a provided AuthService instance.
     * This constructor is useful for testing with mock services.
     * 
     * @param authService the AuthService instance to use
     */
    public RegisterServlet(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Displays the registration form.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Forward to registration form JSP
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }
    
    /**
     * Processes registration form submission.
     * 
     * This method:
     * 1. Validates all input fields (non-empty, valid email, passwords match)
     * 2. Calls AuthService.register() if validation passes
     * 3. On success: creates authenticated session and redirects to dashboard
     * 4. On failure: preserves input and displays errors
     * 
     * @param req the HTTP request containing form data
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Extract form parameters
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        
        // Validate input (Requirements 1.3, 1.4, 1.5, 9.2)
        Map<String, String> errors = new HashMap<>();
        
        // Validate non-empty fields (Requirement 1.3)
        if (!ValidationUtil.isNonEmpty(name)) {
            errors.put("name", "Name is required");
        }
        
        if (!ValidationUtil.isNonEmpty(email)) {
            errors.put("email", "Email is required");
        } else if (!ValidationUtil.isValidEmail(email)) {
            // Validate email format (Requirement 1.4)
            errors.put("email", "Invalid email format");
        }
        
        if (!ValidationUtil.isNonEmpty(password)) {
            errors.put("password", "Password is required");
        }
        
        if (!ValidationUtil.isNonEmpty(confirmPassword)) {
            errors.put("confirmPassword", "Password confirmation is required");
        } else if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            // Validate passwords match (Requirement 1.5)
            errors.put("confirmPassword", "Passwords do not match");
        }
        
        // If validation errors exist, redisplay form with errors (Requirement 10.4)
        if (!errors.isEmpty()) {
            // Preserve input (except passwords)
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("errors", errors);
            
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        
        // Call AuthService to register user (Requirements 1.1, 1.2)
        RegisterResult result = authService.register(name, email, password);
        
        if (result.isSuccess()) {
            // Registration successful - create authenticated session (Requirement 1.6)
            SessionManager.setUserId(req.getSession(), result.getUserId());
            
            // Redirect to dashboard
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            // Registration failed - preserve input and display errors (Requirements 9.2, 10.4)
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("errors", result.getErrors());
            
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
