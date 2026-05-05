package com.wealthfocus.service;

import com.wealthfocus.dao.ExpenseDAO;
import com.wealthfocus.dao.IncomeDAO;
import com.wealthfocus.model.Expense;
import com.wealthfocus.model.Income;
import com.wealthfocus.model.Recommendation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FinanceService {

    private final IncomeDAO incomeDAO = new IncomeDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    public BigDecimal netSavings(String userId, LocalDate start, LocalDate end) throws Exception {
        BigDecimal income = incomeDAO.sumByUserAndRange(userId, start, end);
        BigDecimal expense = expenseDAO.sumByUserAndRange(userId, start, end);
        return income.subtract(expense);
    }

    public Map<Integer, BigDecimal[]> monthlyBreakdown(List<Income> incomes, List<Expense> expenses) {
        Map<Integer, BigDecimal[]> map = new LinkedHashMap<>();
        for (int m = 0; m < 12; m++) {
            map.put(m, new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
        }
        for (Income i : incomes) {
            int m = i.getDate().getMonthValue() - 1;
            map.get(m)[0] = map.get(m)[0].add(i.getAmount());
        }
        for (Expense e : expenses) {
            int m = e.getDate().getMonthValue() - 1;
            map.get(m)[1] = map.get(m)[1].add(e.getAmount());
        }
        return map;
    }

    public Map<String, BigDecimal> categoryBreakdown(List<Expense> expenses) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Expense e : expenses) {
            String key = e.getCategoryName() != null ? e.getCategoryName() : "Unknown";
            map.merge(key, e.getAmount(), BigDecimal::add);
        }
        return map;
    }

    public int creditScore(BigDecimal netSavings, BigDecimal totalIncome) {
        if (totalIncome == null || totalIncome.signum() <= 0) return 350;
        BigDecimal ratio = netSavings.divide(totalIncome, 4, RoundingMode.HALF_UP);
        if (ratio.signum() < 0) ratio = BigDecimal.ZERO;
        if (ratio.compareTo(BigDecimal.ONE) > 0) ratio = BigDecimal.ONE;
        return 350 + ratio.multiply(BigDecimal.valueOf(500)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public String scoreLabel(int score) {
        if (score >= 800) return "Excellent";
        if (score >= 700) return "Good";
        if (score >= 600) return "Fair";
        return "Poor";
    }

    public List<Recommendation> mockRecommendations(BigDecimal netSavings) {
        List<Recommendation> out = new ArrayList<>();
        if (netSavings == null || netSavings.signum() <= 0) return out;
        BigDecimal lowAmt = netSavings.multiply(BigDecimal.valueOf(0.50));
        BigDecimal medAmt = netSavings.multiply(BigDecimal.valueOf(0.30));
        BigDecimal highAmt = netSavings.multiply(BigDecimal.valueOf(0.20));

        out.add(new Recommendation(
                "Fixed Deposit (Bank)",
                "Park surplus cash in a Nepali commercial-bank fixed deposit for guaranteed returns.",
                "low",
                lowAmt.setScale(2, RoundingMode.HALF_UP),
                "Capital protection with stable interest income; ideal for emergency-fund growth."));
        out.add(new Recommendation(
                "Mutual Funds (NEPSE)",
                "Diversified equity-mutual-fund schemes from NIBL, NMB, or Sanima.",
                "medium",
                medAmt.setScale(2, RoundingMode.HALF_UP),
                "Balanced risk-return profile with professional management; suitable for medium-term goals."));
        out.add(new Recommendation(
                "Direct Equity (NEPSE)",
                "Blue-chip stocks listed on Nepal Stock Exchange.",
                "high",
                highAmt.setScale(2, RoundingMode.HALF_UP),
                "Higher potential returns over a long horizon; allocate only the portion you can leave invested."));
        return out;
    }
}
