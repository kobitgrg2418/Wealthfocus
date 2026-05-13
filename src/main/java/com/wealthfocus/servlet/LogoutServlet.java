package com.wealthfocus.servlet;

import com.wealthfocus.util.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet that handles user logout.
 * 
 * This servlet processes logout requests by invalidating the user's session
 * and redirecting to the login page. For security reasons, logout is only
 * handled via POST requests to prevent CSRF attacks.
 * 
 * Requirements: 6.1, 6.2, 6.3
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    /**
     * Processes logout requests.
     * 
     * This method:
     * 1. Retrieves the current session
     * 2. Calls SessionManager.invalidate() to clear all session data
     * 3. Redirects to the login page
     * 
     * @param req the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Get the current session (don't create a new one if it doesn't exist)
        HttpSession session = req.getSession(false);
        
        // Invalidate the session if it exists (Requirements 6.1, 6.3)
        if (session != null) {
            SessionManager.invalidate(session);
        }
        
        // Redirect to login page (Requirement 6.2)
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
