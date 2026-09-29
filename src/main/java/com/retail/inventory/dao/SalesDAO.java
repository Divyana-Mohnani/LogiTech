package com.retail.inventory.dao;

import com.retail.inventory.model.SalesBill;
import com.retail.inventory.model.SalesItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for POS Sales transactions and automatic stock decrements.
 */
public class SalesDAO {
    private static final Logger LOGGER = Logger.getLogger(SalesDAO.class.getName());

    /**
     * Generates a unique invoice number: e.g. INV-2026-84920
     */
    public synchronized String generateNextBillNo() {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        long seq = (System.currentTimeMillis() / 1000) % 90000 + 10000;
        return "INV-" + year + "-" + seq;
    }

    /**
     * Atomically saves a sales bill, saves its line items, and decrements stock.
     * Guaranteed safe across concurrent checkouts via JDBC transaction boundary.
     *
     * @param bill SalesBill containing items
     * @return true if successful
     * @throws SQLException if stock is insufficient or database error occurs
     */
    public boolean saveSalesBill(SalesBill bill) throws SQLException {
        if (bill.getItems() == null || bill.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty cart");
        }

        if (bill.getBillNo() == null || bill.getBillNo().trim().isEmpty()) {
            bill.setBillNo(generateNextBillNo());
        }

        String insertBillSql = "INSERT INTO sales_bill (bill_no, customer_name, customer_phone, subtotal, discount_amount, tax_amount, net_total, payment_mode, cashier_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertItemSql = "INSERT INTO sales_item (bill_no, variant_code, design_name, quantity, unit_mrp, discount_percent, line_total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Stock decrement query ensuring non-negative inventory
        String decrementStockSql = "UPDATE stock SET quantity = quantity - ? WHERE variant_code = ? AND quantity >= ? LIMIT 1";

        Connection conn = null;
        PreparedStatement billStmt = null;
        PreparedStatement itemStmt = null;
        PreparedStatement stockStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert Sales Bill
            billStmt = conn.prepareStatement(insertBillSql);
            billStmt.setString(1, bill.getBillNo());
            billStmt.setString(2, bill.getCustomerName() != null ? bill.getCustomerName() : "Walk-in Customer");
            billStmt.setString(3, bill.getCustomerPhone() != null ? bill.getCustomerPhone() : "");
            billStmt.setBigDecimal(4, bill.getSubtotal());
            billStmt.setBigDecimal(5, bill.getDiscountAmount());
            billStmt.setBigDecimal(6, bill.getTaxAmount());
            billStmt.setBigDecimal(7, bill.getNetTotal());
            billStmt.setString(8, bill.getPaymentMode() != null ? bill.getPaymentMode() : "CASH");
            if (bill.getCashierId() != null) {
                billStmt.setInt(9, bill.getCashierId());
            } else {
                billStmt.setNull(9, java.sql.Types.INTEGER);
            }
            billStmt.executeUpdate();

            // 2. Insert line items & decrement stock
            itemStmt = conn.prepareStatement(insertItemSql);
            stockStmt = conn.prepareStatement(decrementStockSql);

            for (SalesItem item : bill.getItems()) {
                // Insert Item
                itemStmt.setString(1, bill.getBillNo());
                itemStmt.setString(2, item.getVariantCode());
                itemStmt.setString(3, item.getDesignName());
                itemStmt.setInt(4, item.getQuantity());
                itemStmt.setBigDecimal(5, item.getUnitMrp());
                itemStmt.setBigDecimal(6, item.getDiscountPercent());
                itemStmt.setBigDecimal(7, item.getLineTotal());
                itemStmt.addBatch();

                // Decrement Stock
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setString(2, item.getVariantCode());
                stockStmt.setInt(3, item.getQuantity());
                int rowsUpdated = stockStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    conn.rollback();
                    throw new SQLException("Insufficient stock in inventory for variant: " + item.getVariantCode());
                }
            }

            itemStmt.executeBatch();

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed rollback on billing transaction", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Failed saving sales bill: " + bill.getBillNo(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {}
            }
            DBConnection.close(null, billStmt, null);
            DBConnection.close(null, itemStmt, null);
            DBConnection.close(conn, stockStmt, null);
        }
    }

    /**
     * Retrieve complete bill and line items for receipt printing.
     */
    public SalesBill getBillByNo(String billNo) {
        String billSql = "SELECT b.bill_no, b.customer_name, b.customer_phone, b.subtotal, b.discount_amount, b.tax_amount, b.net_total, b.payment_mode, b.bill_date, b.cashier_id, u.full_name AS cashier_name " +
                "FROM sales_bill b " +
                "LEFT JOIN app_user u ON b.cashier_id = u.user_id " +
                "WHERE b.bill_no = ?";

        String itemsSql = "SELECT si.item_id, si.bill_no, si.variant_code, si.design_name, si.quantity, si.unit_mrp, si.discount_percent, si.line_total, " +
                "       v.size, c.colour_name " +
                "FROM sales_item si " +
                "LEFT JOIN variant v ON si.variant_code = v.variant_code " +
                "LEFT JOIN colour c ON v.colour_code = c.colour_code " +
                "WHERE si.bill_no = ? ORDER BY si.item_id ASC";

        Connection conn = null;
        PreparedStatement bStmt = null;
        PreparedStatement iStmt = null;
        ResultSet bRs = null;
        ResultSet iRs = null;

        try {
            conn = DBConnection.getConnection();
            bStmt = conn.prepareStatement(billSql);
            bStmt.setString(1, billNo);
            bRs = bStmt.executeQuery();

            if (bRs.next()) {
                SalesBill bill = new SalesBill();
                bill.setBillNo(bRs.getString("bill_no"));
                bill.setCustomerName(bRs.getString("customer_name"));
                bill.setCustomerPhone(bRs.getString("customer_phone"));
                bill.setSubtotal(bRs.getBigDecimal("subtotal"));
                bill.setDiscountAmount(bRs.getBigDecimal("discount_amount"));
                bill.setTaxAmount(bRs.getBigDecimal("tax_amount"));
                bill.setNetTotal(bRs.getBigDecimal("net_total"));
                bill.setPaymentMode(bRs.getString("payment_mode"));
                bill.setBillDate(bRs.getTimestamp("bill_date"));
                bill.setCashierId(bRs.getInt("cashier_id"));
                bill.setCashierName(bRs.getString("cashier_name"));

                // Fetch items
                iStmt = conn.prepareStatement(itemsSql);
                iStmt.setString(1, billNo);
                iRs = iStmt.executeQuery();
                while (iRs.next()) {
                    SalesItem item = new SalesItem();
                    item.setItemId(iRs.getInt("item_id"));
                    item.setBillNo(iRs.getString("bill_no"));
                    item.setVariantCode(iRs.getString("variant_code"));
                    item.setDesignName(iRs.getString("design_name"));
                    item.setQuantity(iRs.getInt("quantity"));
                    item.setUnitMrp(iRs.getBigDecimal("unit_mrp"));
                    item.setDiscountPercent(iRs.getBigDecimal("discount_percent"));
                    item.setLineTotal(iRs.getBigDecimal("line_total"));
                    item.setSize(iRs.getString("size"));
                    item.setColourName(iRs.getString("colour_name"));
                    bill.addItem(item);
                }
                return bill;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching bill: " + billNo, e);
        } finally {
            DBConnection.close(null, iStmt, iRs);
            DBConnection.close(conn, bStmt, bRs);
        }
        return null;
    }

    /**
     * Retrieve recent sales bills for the sales history register.
     */
    public List<SalesBill> getRecentBills(int limit) {
        List<SalesBill> list = new ArrayList<>();
        String sql = "SELECT b.bill_no, b.customer_name, b.customer_phone, b.net_total, b.payment_mode, b.bill_date, u.full_name AS cashier_name, " +
                "       (SELECT COUNT(*) FROM sales_item WHERE bill_no = b.bill_no) AS item_count " +
                "FROM sales_bill b " +
                "LEFT JOIN app_user u ON b.cashier_id = u.user_id " +
                "ORDER BY b.bill_date DESC LIMIT ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                SalesBill b = new SalesBill();
                b.setBillNo(rs.getString("bill_no"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setCustomerPhone(rs.getString("customer_phone"));
                b.setNetTotal(rs.getBigDecimal("net_total"));
                b.setPaymentMode(rs.getString("payment_mode"));
                b.setBillDate(rs.getTimestamp("bill_date"));
                b.setCashierName(rs.getString("cashier_name"));
                list.add(b);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching recent bills", e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Get today's sales summary.
     */
    public Map<String, Object> getTodaySalesSummary() {
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT " +
                "  COUNT(DISTINCT b.bill_no) AS today_bills, " +
                "  COALESCE(SUM(b.net_total), 0) AS today_revenue, " +
                "  COALESCE(SUM(si.quantity), 0) AS today_units " +
                "FROM sales_bill b " +
                "LEFT JOIN sales_item si ON b.bill_no = si.bill_no " +
                "WHERE DATE(b.bill_date) = CURDATE()";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                map.put("todayBills", rs.getInt("today_bills"));
                map.put("todayRevenue", rs.getBigDecimal("today_revenue"));
                map.put("todayUnits", rs.getInt("today_units"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching today sales summary", e);
            map.put("todayBills", 0);
            map.put("todayRevenue", BigDecimal.ZERO);
            map.put("todayUnits", 0);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return map;
    }
}
