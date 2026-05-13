package com.wealthfocus.servlet;

import com.wealthfocus.dao.ExpenseDAO;
import com.wealthfocus.util.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet(urlPatterns = { "/expense/add", "/expense/delete" })
public class ExpenseServlet extends HttpServlet {

    private final ExpenseDAO dao = new ExpenseDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        
        // Retrieve user ID from session
        Optional<String> userIdOpt = SessionManager.getUserId(req.getSession());
        
        // Handle edge case where user ID is not in session
        // This should not occur due to AuthenticationFilter, but handle gracefully
        if (!userIdOpt.isPresent()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String userId = userIdOpt.get();
        try {
            if ("/expense/add".equals(path)) {
                BigDecimal amount = new BigDecimal(req.getParameter("amount"));
                String description = trim(req.getParameter("description"));
                String categoryId = trim(req.getParameter("categoryId"));
                LocalDate date = LocalDate.parse(req.getParameter("date"));
                if (amount.signum() <= 0 || description == null || description.isEmpty() || categoryId == null || categoryId.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/?error=Invalid+expense+input");
                    return;
                }
                dao.insert(userId, amount, description, categoryId, date);
            } else if ("/expense/delete".equals(path)) {
                String id = req.getParameter("id");
                if (id != null) dao.delete(id, userId);
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/?error=" + (e.getMessage() == null ? "Error" : e.getMessage().replace(" ", "+")));
            return;
        }
        String period = req.getParameter("period");
        String suffix = period != null && !period.isEmpty() ? "?period=" + period : "";
        resp.sendRedirect(req.getContextPath() + "/" + suffix);
    }

    private String trim(String s) { return s == null ? null : s.trim(); }
}
