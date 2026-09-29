package com.retail.inventory.service;

import com.retail.inventory.dao.DBConnection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service to export and backup inventory data into standard SQL statements.
 * Protects store owner against hardware or computer failures.
 */
public class BackupService {
    private static final Logger LOGGER = Logger.getLogger(BackupService.class.getName());

    public void exportDatabaseAsSql(PrintWriter writer) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        writer.println("-- ====================================================================");
        writer.println("-- INVENTORY MANAGEMENT SYSTEM DATABASE BACKUP");
        writer.println("-- Generated on: " + timestamp);
        writer.println("-- ====================================================================");
        writer.println();
        writer.println("SET FOREIGN_KEY_CHECKS = 0;");
        writer.println();

        String[] tables = {"app_user", "colour", "stock_condition", "design", "variant", "stock"};

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();

            for (String table : tables) {
                writer.println("-- --------------------------------------------------------");
                writer.println("-- Table: " + table);
                writer.println("-- --------------------------------------------------------");

                rs = stmt.executeQuery("SELECT * FROM " + table);
                int colCount = rs.getMetaData().getColumnCount();

                while (rs.next()) {
                    StringBuilder sb = new StringBuilder("INSERT INTO ").append(table).append(" VALUES (");
                    for (int i = 1; i <= colCount; i++) {
                        String val = rs.getString(i);
                        if (val == null) {
                            sb.append("NULL");
                        } else {
                            sb.append("'").append(val.replace("'", "''").replace("\\", "\\\\")).append("'");
                        }
                        if (i < colCount) sb.append(", ");
                    }
                    sb.append(");");
                    writer.println(sb);
                }
                writer.println();
            }

            writer.println("SET FOREIGN_KEY_CHECKS = 1;");
            writer.println("-- Backup completed successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed during database backup export", e);
            writer.println("-- ERROR GENERATING BACKUP: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }
}
