package com.cowards.onlyarts.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DBContext {

    private static final String DB_NAME = "OnlyArts";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "12345";
    private static DBContext instance;
    private Connection conn;

    private DBContext() {
    }

    public static synchronized DBContext getInstance() {
        if (instance == null) {
            instance = new DBContext();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String url = "jdbc:sqlserver://localhost:1433;DatabaseName=" + DB_NAME
                        + ";encrypt=true;trustServerCertificate=true;";
                conn = DriverManager.getConnection(url, DB_USERNAME, DB_PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            logError("Exception found on getConnection() method", ex);
        }
        return conn;
    }

    public boolean closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    return true;
                }
            } catch (SQLException ex) {
                logError("Exception found on closeConnection() method", ex);
            }
        }
        return false;
    }

    public boolean closeStatement(PreparedStatement stm) {
        if (stm != null) {
            try {
                stm.close();
                return true;
            } catch (SQLException ex) {
                logError("Exception found on closeStatement() method", ex);
            }
        }
        return false;
    }

    public boolean closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                return true;
            } catch (SQLException ex) {
                logError("Exception found on closeResultSet() method", ex);
            }
        }
        return false;
    }

    private static void logError(String message, Exception ex) {
        Logger.getLogger(DBContext.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
