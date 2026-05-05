package com.wealthfocus.servlet;

import com.wealthfocus.dao.ExpenseDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet(urlPatterns = { "/expense/add", "/expense/delete" })
public class ExpenseServlet extends HttpServlet {

    private final ExpenseDAO dao = new ExpenseDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String userId = InitServlet.DEFAULT_USER_ID;
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
