package com.wealthfocus.dao;

import com.wealthfocus.model.Expense;
import com.wealthfocus.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseDAO {

    public List<Expense> findByUserAndRange(String userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT e.id, e.user_id, e.amount, e.description, e.category_id, c.name AS category_name, e.date " +
                "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ? AND e.date BETWEEN ? AND ? " +
                "ORDER BY e.date DESC, e.created_at DESC";
        List<Expense> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Expense> findAllByUser(String userId) throws SQLException {
        String sql = "SELECT e.id, e.user_id, e.amount, e.description, e.category_id, c.name AS category_name, e.date " +
                "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ? ORDER BY e.date DESC";
        List<Expense> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public String insert(String userId, BigDecimal amount, String description, String categoryId, LocalDate date) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO expenses (id, user_id, amount, description, category_id, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, description);
            ps.setString(5, categoryId);
            ps.setDate(6, Date.valueOf(date));
            ps.executeUpdate();
        }
        return id;
    }

    public boolean delete(String id, String userId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public BigDecimal sumByUserAndRange(String userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE user_id = ? AND date BETWEEN ? AND ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private Expense map(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setId(rs.getString("id"));
        e.setUserId(rs.getString("user_id"));
        e.setAmount(rs.getBigDecimal("amount"));
        e.setDescription(rs.getString("description"));
        e.setCategoryId(rs.getString("category_id"));
        e.setCategoryName(rs.getString("category_name"));
        e.setDate(rs.getDate("date").toLocalDate());
        return e;
    }
}
