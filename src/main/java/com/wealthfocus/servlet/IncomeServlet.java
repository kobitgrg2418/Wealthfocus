package com.wealthfocus.servlet;

import com.wealthfocus.dao.IncomeDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet(urlPatterns = { "/income/add", "/income/delete" })
public class IncomeServlet extends HttpServlet {

    private final IncomeDAO dao = new IncomeDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String userId = InitServlet.DEFAULT_USER_ID;
        try {
            if ("/income/add".equals(path)) {
                BigDecimal amount = new BigDecimal(req.getParameter("amount"));
                String source = trim(req.getParameter("source"));
                LocalDate date = LocalDate.parse(req.getParameter("date"));
                if (amount.signum() <= 0 || source == null || source.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/?error=Invalid+income+input");
                    return;
                }
                dao.insert(userId, amount, source, date);
            } else if ("/income/delete".equals(path)) {
                String id = req.getParameter("id");
                if (id != null) dao.delete(id, userId);
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/?error=" + url(e.getMessage()));
            return;
        }
        String period = req.getParameter("period");
        String suffix = period != null && !period.isEmpty() ? "?period=" + period : "";
        resp.sendRedirect(req.getContextPath() + "/" + suffix);
    }

    private String trim(String s) { return s == null ? null : s.trim(); }
    private String url(String s) {
        return s == null ? "Error" : s.replace(" ", "+");
    }
}
