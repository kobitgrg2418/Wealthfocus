package com.wealthfocus.dao;

import com.wealthfocus.model.User;
import com.wealthfocus.util.DBConnection;

import java.sql.*;
import java.util.UUID;

public class UserDAO {

    /**
     * Creates a new user in the database.
     * Generates a UUID for the user ID if not already set.
     *
     * @param user User object containing name, email, and passwordHash
     * @return The user ID of the created user
     * @throws SQLException if database operation fails
     */
    public String create(User user) throws SQLException {
        // Generate UUID if not already set
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO users (id, name, email, password_hash) VALUES (?, ?, ?, ?)";
        
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.executeUpdate();
        }
        
        return user.getId();
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, photo_path = ? WHERE id = ?";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getPhotoPath());
            ps.setString(6, user.getId());
            ps.executeUpdate();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        try { user.setPasswordHash(rs.getString("password_hash")); } catch (SQLException ignored) {}
        try { user.setPhone(rs.getString("phone")); } catch (SQLException ignored) {}
        try { user.setAddress(rs.getString("address")); } catch (SQLException ignored) {}
        try { user.setPhotoPath(rs.getString("photo_path")); } catch (SQLException ignored) {}
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());
        } catch (SQLException ignored) {}
        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime());
        } catch (SQLException ignored) {}

        return user;
    }
}
