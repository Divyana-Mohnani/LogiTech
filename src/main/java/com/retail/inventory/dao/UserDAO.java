package com.retail.inventory.dao;

import com.retail.inventory.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User authentication and role management.
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authenticates a user by username and password.
     * @param username username
     * @param password plain password (or hashed)
     * @return User object if valid, null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, full_name, role FROM app_user WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user: " + username, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Find user by username.
     */
    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, full_name, role FROM app_user WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user: " + username, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }
}
