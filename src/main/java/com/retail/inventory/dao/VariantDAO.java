package com.retail.inventory.dao;

import com.retail.inventory.model.Variant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Product Variants.
 * Handles structured variant codes (e.g. M/L/B/BK).
 */
public class VariantDAO {
    private static final Logger LOGGER = Logger.getLogger(VariantDAO.class.getName());

    public boolean exists(String variantCode) {
        String sql = "SELECT 1 FROM variant WHERE variant_code = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, variantCode);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking variant existence: " + variantCode, e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    public Variant getVariantByCode(String variantCode) {
        String sql = "SELECT variant_code, design_no, gender, size, length, colour_code, created_at FROM variant WHERE variant_code = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, variantCode);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Variant v = new Variant();
                v.setVariantCode(rs.getString("variant_code"));
                v.setDesignNo(rs.getString("design_no"));
                v.setGender(rs.getString("gender"));
                v.setSize(rs.getString("size"));
                v.setLength(rs.getString("length"));
                v.setColourCode(rs.getString("colour_code"));
                v.setCreatedAt(rs.getTimestamp("created_at"));
                return v;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting variant by code: " + variantCode, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Variant> getVariantsByDesign(String designNo) {
        List<Variant> list = new ArrayList<>();
        String sql = "SELECT variant_code, design_no, gender, size, length, colour_code, created_at FROM variant WHERE design_no = ? ORDER BY variant_code ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, designNo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Variant v = new Variant();
                v.setVariantCode(rs.getString("variant_code"));
                v.setDesignNo(rs.getString("design_no"));
                v.setGender(rs.getString("gender"));
                v.setSize(rs.getString("size"));
                v.setLength(rs.getString("length"));
                v.setColourCode(rs.getString("colour_code"));
                v.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(v);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting variants for design: " + designNo, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public boolean addVariant(Variant variant) {
        String sql = "INSERT INTO variant (variant_code, design_no, gender, size, length, colour_code) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, variant.getVariantCode().trim());
            stmt.setString(2, variant.getDesignNo().trim());
            stmt.setString(3, variant.getGender().trim());
            stmt.setString(4, variant.getSize().trim());
            stmt.setString(5, variant.getLength() != null ? variant.getLength().trim() : "NA");
            stmt.setString(6, variant.getColourCode().trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting variant: " + variant.getVariantCode(), e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }
}
