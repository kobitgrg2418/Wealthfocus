package com.wealthfocus.filter;

import com.wealthfocus.util.SessionManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication filter that protects routes requiring user authentication.
 * 
 * This filter intercepts all requests and checks if the user is authenticated
 * before allowing access to protected routes. Public routes (login, register,
 * static resources) are allowed without authentication.
 * 
 * For unauthenticated users attempting to access protected routes:
 * - The originally requested URL is preserved in the session
 * - The user is redirected to the login page
 * - After successful login, they are redirected to the original URL
 * 
 * For authenticated users:
 * - The request proceeds normally to the requested resource
 * 
 * Requirements: 5.1, 5.2, 5.3, 5.4
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    
    /**
     * Session attribute key for storing the originally requested URL.
     */
    private static final String ORIGINAL_URL_ATTRIBUTE = "originalUrl";
    
    /**
     * Initializes the filter. No initialization is required for this filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }
    
    /**
     * Filters incoming requests to enforce authentication requirements.
     * 
     * This method:
     * 1. Checks if the requested path is a public route
     * 2. If public, allows the request to proceed
     * 3. If protected, checks authentication status via SessionManager
     * 4. If authenticated, allows the request to proceed
     * 5. If unauthenticated, preserves the requested URL and redirects to login
     * 
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Remove context path to get the relative path
        String relativePath = requestPath.substring(contextPath.length());
        
        // Check if this is a public route (Requirement 5.3)
        if (isPublicRoute(relativePath)) {
            // Allow public routes to proceed without authentication
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is authenticated (Requirement 5.1, 5.2)
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null && SessionManager.isAuthenticated(session)) {
            // User is authenticated - allow request to proceed (Requirement 5.2)
            chain.doFilter(request, response);
        } else {
            // User is not authenticated - redirect to login (Requirement 5.1)
            
            // Preserve the originally requested URL for post-login redirect (Requirement 5.4)
            // Only preserve if this is a GET request (not POST/PUT/DELETE)
            if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
                HttpSession newSession = httpRequest.getSession(true);
                
                // Build the full URL including query string if present
                String originalUrl = relativePath;
                String queryString = httpRequest.getQueryString();
                
                if (queryString != null && !queryString.isEmpty()) {
                    originalUrl = originalUrl + "?" + queryString;
                }
                
                newSession.setAttribute(ORIGINAL_URL_ATTRIBUTE, originalUrl);
            }
            
            // Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }
    
    /**
     * Determines if the given path is a public route that does not require authentication.
     * 
     * Public routes include:
     * - /login - the login page
     * - /register - the registration page
     * - /static/* - static resources (CSS, JS, images)
     * 
     * @param path the request path (relative to context root)
     * @return true if the path is public, false if it requires authentication
     */
    private boolean isPublicRoute(String path) {
        // Normalize path to handle trailing slashes
        if (path == null) {
            return false;
        }
        
        // Public routes (Requirement 5.3)
        return path.equals("/login") ||
               path.equals("/register") ||
               path.startsWith("/static/");
    }
    
    /**
     * Destroys the filter. No cleanup is required for this filter.
     */
    @Override
    public void destroy() {
        // No cleanup required
    }
}
