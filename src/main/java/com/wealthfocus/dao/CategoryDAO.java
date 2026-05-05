package com.wealthfocus.dao;

import com.wealthfocus.model.Category;
import com.wealthfocus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> findAvailable(String userId) throws SQLException {
        String sql = "SELECT id, name, is_default, user_id FROM categories " +
                "WHERE user_id IS NULL OR user_id = ? ORDER BY name ASC";
        List<Category> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getBoolean("is_default"),
                            rs.getString("user_id")));
                }
            }
        }
        return list;
    }
}
