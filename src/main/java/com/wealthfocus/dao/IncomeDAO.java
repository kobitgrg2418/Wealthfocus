package com.wealthfocus.dao;

import com.wealthfocus.model.Income;
import com.wealthfocus.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IncomeDAO {

    public List<Income> findByUserAndRange(String userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT id, user_id, amount, source, date FROM incomes " +
                "WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC, created_at DESC";
        List<Income> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Income> findAllByUser(String userId) throws SQLException {
        String sql = "SELECT id, user_id, amount, source, date FROM incomes WHERE user_id = ? ORDER BY date DESC";
        List<Income> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public String insert(String userId, BigDecimal amount, String source, LocalDate date) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO incomes (id, user_id, amount, source, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, source);
            ps.setDate(5, Date.valueOf(date));
            ps.executeUpdate();
        }
        return id;
    }

    public boolean delete(String id, String userId) throws SQLException {
        String sql = "DELETE FROM incomes WHERE id = ? AND user_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public BigDecimal sumByUserAndRange(String userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE user_id = ? AND date BETWEEN ? AND ?";
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

    private Income map(ResultSet rs) throws SQLException {
        return new Income(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getBigDecimal("amount"),
                rs.getString("source"),
                rs.getDate("date").toLocalDate()
        );
    }
}
