package com.wealthfocus.servlet;

import com.google.gson.Gson;
import com.wealthfocus.dao.CategoryDAO;
import com.wealthfocus.dao.ExpenseDAO;
import com.wealthfocus.dao.IncomeDAO;
import com.wealthfocus.model.Category;
import com.wealthfocus.model.Expense;
import com.wealthfocus.model.Income;
import com.wealthfocus.service.FinanceService;
import com.wealthfocus.util.Money;
import com.wealthfocus.util.TimeRangeUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = { "/", "/dashboard" })
public class DashboardServlet extends HttpServlet {

    private final IncomeDAO incomeDAO = new IncomeDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final FinanceService service = new FinanceService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String userId = InitServlet.DEFAULT_USER_ID;
            String preset = req.getParameter("period");
            TimeRangeUtil.Range range = TimeRangeUtil.get(preset);

            List<Income> incomes = incomeDAO.findByUserAndRange(userId, range.start, range.end);
            List<Expense> expenses = expenseDAO.findByUserAndRange(userId, range.start, range.end);
            List<Category> categories = categoryDAO.findAvailable(userId);

            BigDecimal totalIncome = sum(incomes, Income::getAmount);
            BigDecimal totalExpenses = sum(expenses, Expense::getAmount);
            BigDecimal netSavings = totalIncome.subtract(totalExpenses);
            int score = service.creditScore(netSavings, totalIncome);

            // Monthly chart - across full year of all data, not just filtered
            List<Income> allIncomes = incomeDAO.findAllByUser(userId);
            List<Expense> allExpenses = expenseDAO.findAllByUser(userId);
            Map<Integer, BigDecimal[]> monthly = service.monthlyBreakdown(allIncomes, allExpenses);
            List<Double> monthlyIncome = new ArrayList<>();
            List<Double> monthlyExpense = new ArrayList<>();
            for (int m = 0; m < 12; m++) {
                monthlyIncome.add(monthly.get(m)[0].doubleValue());
                monthlyExpense.add(monthly.get(m)[1].doubleValue());
            }

            // Category breakdown
            Map<String, BigDecimal> catMap = service.categoryBreakdown(expenses);
            Map<String, Double> categoryChart = new LinkedHashMap<>();
            for (Map.Entry<String, BigDecimal> e : catMap.entrySet()) {
                categoryChart.put(e.getKey(), e.getValue().doubleValue());
            }

            req.setAttribute("range", range);
            req.setAttribute("incomes", incomes);
            req.setAttribute("expenses", expenses);
            req.setAttribute("categories", categories);
            req.setAttribute("totalIncome", Money.formatNPR(totalIncome));
            req.setAttribute("totalExpenses", Money.formatNPR(totalExpenses));
            req.setAttribute("netSavings", Money.formatSigned(netSavings));
            req.setAttribute("netSavingsPositive", netSavings.signum() > 0);
            req.setAttribute("netSavingsNonNegative", netSavings.signum() >= 0);
            req.setAttribute("score", score);
            req.setAttribute("scoreLabel", service.scoreLabel(score));
            req.setAttribute("monthlyIncomeJson", gson.toJson(monthlyIncome));
            req.setAttribute("monthlyExpenseJson", gson.toJson(monthlyExpense));
            req.setAttribute("categoryChartJson", gson.toJson(categoryChart));

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private interface Getter<T> { BigDecimal apply(T t); }

    private <T> BigDecimal sum(List<T> list, Getter<T> g) {
        BigDecimal total = BigDecimal.ZERO;
        for (T t : list) total = total.add(g.apply(t));
        return total;
    }
}
