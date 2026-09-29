package com.retail.inventory.dao;

import com.retail.inventory.model.Colour;
import com.retail.inventory.model.StockCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Master tables: Colour and Condition.
 */
public class MasterDAO {
    private static final Logger LOGGER = Logger.getLogger(MasterDAO.class.getName());

    // ==========================================
    // COLOUR METHODS
    // ==========================================

    public List<Colour> getAllColours() {
        List<Colour> list = new ArrayList<>();
        String sql = "SELECT colour_code, colour_name FROM colour ORDER BY colour_name ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Colour(rs.getString("colour_code"), rs.getString("colour_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching colours", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public Colour getColourByCode(String code) {
        String sql = "SELECT colour_code, colour_name FROM colour WHERE colour_code = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new Colour(rs.getString("colour_code"), rs.getString("colour_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching colour by code: " + code, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public boolean addColour(Colour colour) {
        String sql = "INSERT INTO colour (colour_code, colour_name) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, colour.getColourCode().toUpperCase());
            stmt.setString(2, colour.getColourName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting colour: " + colour.getColourCode(), e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }

    // ==========================================
    // STOCK CONDITION METHODS
    // ==========================================

    public List<StockCondition> getAllConditions() {
        List<StockCondition> list = new ArrayList<>();
        String sql = "SELECT condition_id, condition_name FROM stock_condition ORDER BY condition_id ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new StockCondition(rs.getInt("condition_id"), rs.getString("condition_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching conditions", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public StockCondition getConditionById(int id) {
        String sql = "SELECT condition_id, condition_name FROM stock_condition WHERE condition_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new StockCondition(rs.getInt("condition_id"), rs.getString("condition_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching condition by id: " + id, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public StockCondition getConditionByName(String name) {
        String sql = "SELECT condition_id, condition_name FROM stock_condition WHERE LOWER(condition_name) = LOWER(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new StockCondition(rs.getInt("condition_id"), rs.getString("condition_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching condition by name: " + name, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }
}
