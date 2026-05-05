package com.wealthfocus.servlet;

import com.google.gson.Gson;
import com.wealthfocus.dao.ExpenseDAO;
import com.wealthfocus.dao.IncomeDAO;
import com.wealthfocus.model.Recommendation;
import com.wealthfocus.service.FinanceService;
import com.wealthfocus.util.TimeRangeUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/advice")
public class InvestmentServlet extends HttpServlet {

    private final FinanceService service = new FinanceService();
    private final IncomeDAO incomeDAO = new IncomeDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String userId = InitServlet.DEFAULT_USER_ID;
            TimeRangeUtil.Range r = TimeRangeUtil.get(req.getParameter("period"));
            BigDecimal income = incomeDAO.sumByUserAndRange(userId, r.start, r.end);
            BigDecimal expense = expenseDAO.sumByUserAndRange(userId, r.start, r.end);
            BigDecimal net = income.subtract(expense);
            List<Recommendation> recs = service.mockRecommendations(net);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(recs));
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
