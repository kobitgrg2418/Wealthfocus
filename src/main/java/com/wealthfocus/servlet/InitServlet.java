package com.wealthfocus.servlet;

import com.wealthfocus.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(value = "/__init", loadOnStartup = 1)
public class InitServlet extends HttpServlet {

    public static final String DEFAULT_USER_ID = "00000000-0000-0000-0000-000000000001";

    @Override
    public void init() throws ServletException {
        try (Connection c = DBConnection.get()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE id = ?")) {
                ps.setString(1, DEFAULT_USER_ID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users (id, email, name) VALUES (?, ?, ?)")) {
                ps.setString(1, DEFAULT_USER_ID);
                ps.setString(2, "demo@wealthfocus.local");
                ps.setString(3, "Jhon");
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize default user", e);
        }
    }
}
