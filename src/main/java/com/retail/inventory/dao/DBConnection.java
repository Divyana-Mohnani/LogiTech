package com.retail.inventory.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection utility loading configuration from db.properties.
 * Provides thread-safe connections using JDBC.
 */
public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final Properties properties = new Properties();

    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                properties.load(in);
                driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&allowPublicKeyRetrieval=true");
                username = properties.getProperty("db.user", "root");
                password = properties.getProperty("db.password", "");
            } else {
                LOGGER.warning("db.properties not found in classpath. Using defaults.");
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&allowPublicKeyRetrieval=true";
                username = "root";
                password = "";
            }

            Class.forName(driver);
            LOGGER.info("Database driver loaded successfully: " + driver);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection configuration", e);
        }
    }

    /**
     * Obtains a new database connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Safely closes a Connection, Statement, and ResultSet.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Statement stmt, ResultSet rs) {
        close(null, stmt, rs);
    }

    public static void close(Statement stmt) {
        close(null, stmt, null);
    }
}
