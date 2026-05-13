package com.wealthfocus.servlet;

import com.wealthfocus.service.AuthService;
import com.wealthfocus.service.AuthResult;
import com.wealthfocus.util.SessionManager;
import com.wealthfocus.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet that handles user login.
 * 
 * This servlet provides two main functions:
 * - GET: Displays the login form
 * - POST: Processes login submissions with validation
 * 
 * On successful login, the user session is created and the user is redirected
 * to the dashboard (or the originally requested URL if preserved). On failure,
 * the form is redisplayed with error messages and preserved email input.
 * 
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 5.4, 9.1, 9.4, 10.1, 10.4
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private final AuthService authService;
    
    /**
     * Session attribute key for storing the originally requested URL.
     * This is used to redirect users back to their intended destination after login.
     */
    private static final String ORIGINAL_URL_ATTRIBUTE = "originalUrl";
    
    /**
     * Creates a LoginServlet with a new AuthService instance.
     */
    public LoginServlet() {
        this.authService = new AuthService();
    }
    
    /**
     * Creates a LoginServlet with a provided AuthService instance.
     * This constructor is useful for testing with mock services.
     * 
     * @param authService the AuthService instance to use
     */
    public LoginServlet(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Displays the login form.
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Forward to login form JSP (Requirement 10.1)
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
    
    /**
     * Processes login form submission.
     * 
     * This method:
     * 1. Validates input fields (non-empty email and password)
     * 2. Calls AuthService.authenticate() if validation passes
     * 3. On success: creates authenticated session and redirects to dashboard or original URL
     * 4. On failure: preserves email input and displays error message
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
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        // Validate input (Requirements 3.4, 9.1)
        Map<String, String> errors = new HashMap<>();
        
        // Validate non-empty fields (Requirement 3.4)
        if (!ValidationUtil.isNonEmpty(email)) {
            errors.put("email", "Email is required");
        }
        
        if (!ValidationUtil.isNonEmpty(password)) {
            errors.put("password", "Password is required");
        }
        
        // If validation errors exist, redisplay form with errors (Requirement 10.4)
        if (!errors.isEmpty()) {
            // Preserve email input (not password for security)
            req.setAttribute("email", email);
            req.setAttribute("errors", errors);
            
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }
        
        // Call AuthService to authenticate user (Requirements 3.1, 3.2, 3.3)
        AuthResult result = authService.authenticate(email, password);
        
        if (result.isSuccess()) {
            // Authentication successful - create authenticated session (Requirements 3.1, 3.5)
            HttpSession session = req.getSession();
            SessionManager.setUserId(session, result.getUserId());
            
            // Check if there's an original URL to redirect to (Requirement 5.4)
            String originalUrl = (String) session.getAttribute(ORIGINAL_URL_ATTRIBUTE);
            
            if (originalUrl != null) {
                // Remove the original URL from session
                session.removeAttribute(ORIGINAL_URL_ATTRIBUTE);
                
                // Redirect to the originally requested URL
                resp.sendRedirect(req.getContextPath() + originalUrl);
            } else {
                // No original URL - redirect to dashboard
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }
        } else {
            // Authentication failed - preserve email and display error (Requirements 9.1, 9.4, 10.4)
            req.setAttribute("email", email);
            
            // Create errors map with the authentication error message
            errors.put("general", result.getErrorMessage());
            req.setAttribute("errors", errors);
            
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
