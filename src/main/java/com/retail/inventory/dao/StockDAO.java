package com.retail.inventory.dao;

import com.retail.inventory.model.Stock;
import com.retail.inventory.model.StockItemView;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Stock management, inventory search, valuation, and condition alerts.
 */
public class StockDAO {
    private static final Logger LOGGER = Logger.getLogger(StockDAO.class.getName());

    private static final String BASE_JOIN_QUERY =
            "SELECT s.stock_id, s.variant_code, s.quantity, s.vendor_price, s.mrp, s.condition_id, s.note, s.updated_at, " +
            "       v.design_no, v.gender, v.size, v.length, v.colour_code, " +
            "       d.design_name, d.category, " +
            "       c.colour_name, sc.condition_name " +
            "FROM stock s " +
            "JOIN variant v ON s.variant_code = v.variant_code " +
            "JOIN design d ON v.design_no = d.design_no " +
            "JOIN colour c ON v.colour_code = c.colour_code " +
            "JOIN stock_condition sc ON s.condition_id = sc.condition_id ";

    /**
     * Map a ResultSet row to a StockItemView.
     */
    private StockItemView mapRowToView(ResultSet rs) throws SQLException {
        StockItemView view = new StockItemView();
        view.setStockId(rs.getInt("stock_id"));
        view.setVariantCode(rs.getString("variant_code"));
        view.setQuantity(rs.getInt("quantity"));
        view.setVendorPrice(rs.getBigDecimal("vendor_price"));
        view.setMrp(rs.getBigDecimal("mrp"));
        view.setConditionId(rs.getInt("condition_id"));
        view.setNote(rs.getString("note"));
        view.setUpdatedAt(rs.getTimestamp("updated_at"));

        view.setDesignNo(rs.getString("design_no"));
        view.setDesignName(rs.getString("design_name"));
        view.setCategory(rs.getString("category"));
        view.setGender(rs.getString("gender"));
        view.setSize(rs.getString("size"));
        view.setLength(rs.getString("length"));
        view.setColourCode(rs.getString("colour_code"));
        view.setColourName(rs.getString("colour_name"));
        view.setConditionName(rs.getString("condition_name"));
        return view;
    }

    /**
     * Add new stock line or increment quantity if variant + condition already exists.
     */
    public boolean addOrUpdateStock(String variantCode, int quantity, BigDecimal vendorPrice, BigDecimal mrp, int conditionId, String note) {
        String checkSql = "SELECT stock_id, quantity FROM stock WHERE variant_code = ? AND condition_id = ?";
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, variantCode);
            checkStmt.setInt(2, conditionId);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Stock line exists for this condition -> increment quantity and update price/note
                int existingId = rs.getInt("stock_id");
                int newQty = rs.getInt("quantity") + quantity;
                String updateSql = "UPDATE stock SET quantity = ?, vendor_price = ?, mrp = ?, note = ? WHERE stock_id = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, Math.max(0, newQty));
                updateStmt.setBigDecimal(2, vendorPrice);
                updateStmt.setBigDecimal(3, mrp);
                updateStmt.setString(4, note);
                updateStmt.setInt(5, existingId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert fresh stock line
                String insertSql = "INSERT INTO stock (variant_code, quantity, vendor_price, mrp, condition_id, note) VALUES (?, ?, ?, ?, ?, ?)";
                updateStmt = conn.prepareStatement(insertSql);
                updateStmt.setString(1, variantCode);
                updateStmt.setInt(2, quantity);
                updateStmt.setBigDecimal(3, vendorPrice);
                updateStmt.setBigDecimal(4, mrp);
                updateStmt.setInt(5, conditionId);
                updateStmt.setString(6, note);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in addOrUpdateStock for variant: " + variantCode, e);
            return false;
        } finally {
            DBConnection.close(null, updateStmt, null);
            DBConnection.close(conn, checkStmt, rs);
        }
    }

    /**
     * Get aggregated stock view by stock ID.
     */
    public StockItemView getStockViewById(int stockId) {
        String sql = BASE_JOIN_QUERY + " WHERE s.stock_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stockId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToView(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching stock view by id: " + stockId, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Look up stock item by exact variant code (scanned via barcode scanner).
     */
    public StockItemView findByVariantCode(String variantCode) {
        String sql = BASE_JOIN_QUERY + " WHERE s.variant_code = ? ORDER BY s.condition_id ASC LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, variantCode.trim());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToView(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up variant code: " + variantCode, e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Search stock by multiple criteria (synopsis page 11 & 14).
     */
    public List<StockItemView> searchStock(String query, String category, String gender, Integer conditionId, String colourCode) {
        List<StockItemView> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_JOIN_QUERY).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (s.variant_code LIKE ? OR v.design_no LIKE ? OR d.design_name LIKE ? OR s.note LIKE ?) ");
            String qLike = "%" + query.trim() + "%";
            params.add(qLike);
            params.add(qLike);
            params.add(qLike);
            params.add(qLike);
        }

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append(" AND d.category = ? ");
            params.add(category.trim());
        }

        if (gender != null && !gender.trim().isEmpty() && !"ALL".equalsIgnoreCase(gender)) {
            sql.append(" AND v.gender = ? ");
            params.add(gender.trim());
        }

        if (conditionId != null && conditionId > 0) {
            sql.append(" AND s.condition_id = ? ");
            params.add(conditionId);
        }

        if (colourCode != null && !colourCode.trim().isEmpty() && !"ALL".equalsIgnoreCase(colourCode)) {
            sql.append(" AND v.colour_code = ? ");
            params.add(colourCode.trim());
        }

        sql.append(" ORDER BY s.updated_at DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToView(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching stock", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Fetches problem stock lines (Defective, Old, Dead stock) for actionable reporting.
     */
    public List<StockItemView> getProblemStock(Integer conditionFilter) {
        List<StockItemView> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_JOIN_QUERY);
        if (conditionFilter != null && conditionFilter > 0) {
            sql.append(" WHERE s.condition_id = ? ");
        } else {
            sql.append(" WHERE s.condition_id IN (2, 3, 4) "); // 2=Defective, 3=Old, 4=Dead stock
        }
        sql.append(" ORDER BY s.condition_id ASC, s.quantity DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            if (conditionFilter != null && conditionFilter > 0) {
                stmt.setInt(1, conditionFilter);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToView(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching problem stock", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Update stock line details directly.
     */
    public boolean updateStock(int stockId, int quantity, BigDecimal vendorPrice, BigDecimal mrp, int conditionId, String note) {
        String sql = "UPDATE stock SET quantity = ?, vendor_price = ?, mrp = ?, condition_id = ?, note = ? WHERE stock_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Math.max(0, quantity));
            stmt.setBigDecimal(2, vendorPrice);
            stmt.setBigDecimal(3, mrp);
            stmt.setInt(4, conditionId);
            stmt.setString(5, note);
            stmt.setInt(6, stockId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock line: " + stockId, e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }

    /**
     * Delete stock line.
     */
    public boolean deleteStock(int stockId) {
        String sql = "DELETE FROM stock WHERE stock_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stockId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting stock: " + stockId, e);
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }

    /**
     * Get inventory summary metrics for dashboard and reports.
     */
    public Map<String, Object> getInventoryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        String sql = "SELECT " +
                "  COUNT(DISTINCT v.design_no) AS total_designs, " +
                "  COUNT(DISTINCT v.variant_code) AS total_variants, " +
                "  COALESCE(SUM(s.quantity), 0) AS total_items, " +
                "  COALESCE(SUM(s.quantity * s.vendor_price), 0) AS total_cost_value, " +
                "  COALESCE(SUM(s.quantity * s.mrp), 0) AS total_mrp_value, " +
                "  COALESCE(SUM(CASE WHEN s.condition_id = 1 THEN s.quantity ELSE 0 END), 0) AS normal_items, " +
                "  COALESCE(SUM(CASE WHEN s.condition_id = 2 THEN s.quantity ELSE 0 END), 0) AS defective_items, " +
                "  COALESCE(SUM(CASE WHEN s.condition_id = 3 THEN s.quantity ELSE 0 END), 0) AS old_items, " +
                "  COALESCE(SUM(CASE WHEN s.condition_id = 4 THEN s.quantity ELSE 0 END), 0) AS dead_items, " +
                "  COALESCE(SUM(CASE WHEN s.condition_id = 4 THEN (s.quantity * s.mrp) ELSE 0 END), 0) AS dead_stock_value " +
                "FROM variant v " +
                "LEFT JOIN stock s ON v.variant_code = s.variant_code";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                metrics.put("totalDesigns", rs.getInt("total_designs"));
                metrics.put("totalVariants", rs.getInt("total_variants"));
                metrics.put("totalItems", rs.getInt("total_items"));
                metrics.put("totalCostValue", rs.getBigDecimal("total_cost_value"));
                metrics.put("totalMrpValue", rs.getBigDecimal("total_mrp_value"));
                metrics.put("normalItems", rs.getInt("normal_items"));
                metrics.put("defectiveItems", rs.getInt("defective_items"));
                metrics.put("oldItems", rs.getInt("old_items"));
                metrics.put("deadItems", rs.getInt("dead_items"));
                metrics.put("deadStockValue", rs.getBigDecimal("dead_stock_value"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching inventory metrics", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return metrics;
    }
}
