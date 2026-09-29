package com.retail.inventory.dao;

import com.retail.inventory.model.Design;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Product Designs.
 */
public class DesignDAO {
    private static final Logger LOGGER = Logger.getLogger(DesignDAO.class.getName());

    public List<Design> getAllDesigns() {
        List<Design> list = new ArrayList<>();
        String sql = "SELECT design_no, design_name, category, description, created_at FROM design ORDER BY design_no ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Design d = new Design();
                d.setDesignNo(rs.getString("design_no"));
                d.setDesignName(rs.getString("design_name"));
                d.setCategory(rs.getString("category"));
                d.setDescription(rs.getString("description"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(d);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all designs", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public Design getDesignByNo(String designNo) {
        String sql = "SELECT design_no, design_name, category, description, created_at FROM design WHERE design_no = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, designNo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Design d = new Design();
                d.setDesignNo(rs.getString("design_no"));
                d.setDesignName(rs.getString("design_name"));
                d.setCategory(rs.getString("category"));
                d.setDescription(rs.getString("description"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                return d;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching design: " + designNo, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Design> getDesignsByCategory(String category) {
        List<Design> list = new ArrayList<>();
        String sql = "SELECT design_no, design_name, category, description, created_at FROM design WHERE category = ? ORDER BY design_no ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Design d = new Design();
                d.setDesignNo(rs.getString("design_no"));
                d.setDesignName(rs.getString("design_name"));
                d.setCategory(rs.getString("category"));
                d.setDescription(rs.getString("description"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(d);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching designs by category: " + category, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public boolean addDesign(Design design) {
        String sql = "INSERT INTO design (design_no, design_name, category, description) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, design.getDesignNo().trim().toUpperCase());
            stmt.setString(2, design.getDesignName().trim());
            stmt.setString(3, design.getCategory().trim());
            stmt.setString(4, design.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding design: " + design.getDesignNo(), e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }
}
