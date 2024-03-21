package com.cowards.onlyarts.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code DBContext} class represents a singleton database context for
 * managing database connections and resources. It provides methods for
 * obtaining and closing database connections, statements, and result sets.
 */
public final class DBContext {

    private static final String DB_NAME = "OnlyArts";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "12345";
    private static DBContext instance;
    private Connection conn;

    /**
     * Private constructor to prevent direct instantiation of {@code DBContext}.
     */
    private DBContext() {
    }

    /**
     * Retrieves the singleton instance of {@code DBContext}.
     *
     * @return The singleton instance of {@code DBContext}.
     */
    public static synchronized DBContext getInstance() {
        if (instance == null) {
            instance = new DBContext();
        }
        return instance;
    }

    /**
     * Retrieves a connection to the database. If a connection does not exist or
     * is closed, a new connection is created.
     *
     * @return A connection to the database.
     */
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

    /**
     * Closes the provided database connection.
     *
     * @param conn The connection to be closed.
     * @return {@code true} if the connection was successfully closed,
     * {@code false} otherwise.
     */
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

    /**
     * Closes the provided prepared statement.
     *
     * @param stm The prepared statement to be closed.
     * @return {@code true} if the statement was successfully closed,
     * {@code false} otherwise.
     */
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

    /**
     * Closes the provided result set.
     *
     * @param rs The result set to be closed.
     * @return {@code true} if the result set was successfully closed,
     * {@code false} otherwise.
     */
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

    /**
     * Logs an error message and exception.
     *
     * @param message The error message.
     * @param ex The exception.
     */
    private static void logError(String message, Exception ex) {
        Logger.getLogger(DBContext.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
