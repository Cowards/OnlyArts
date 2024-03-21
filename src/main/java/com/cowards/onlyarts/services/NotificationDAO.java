package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access methods for managing notifications in the database.
 * It allows sending responses to notifications for specific users.
 */
public class NotificationDAO {

    private static NotificationDAO instance;
    private static final DBContext DB = DBContext.getInstance();

    //SQL query
    private static final String SEND_RESOPNSE
            = "INSERT INTO Notifications (user_id, description, status) VALUES (?, ?, ?)";

    /**
     * Logs an error message along with the exception stack trace.
     *
     * @param message The error message to be logged.
     * @param ex The Throwable object representing the exception.
     */
    private static void logError(String message, Throwable ex) {
        Logger.getLogger(NotificationDAO.class.getName()).log(Level.SEVERE, message, ex);
    }

    /**
     * Private constructor to prevent direct instantiation of the
     * NotificationDAO class.
     */
    private NotificationDAO() {
    }

    /**
     * Retrieves an instance of the NotificationDAO class. If no instance
     * exists, a new instance is created and returned.
     *
     * @return An instance of the NotificationDAO class.
     */
    public static NotificationDAO getInstance() {
        if (instance == null) {
            instance = new NotificationDAO();
        }
        return instance;
    }

    /**
     * Sends a response to a notification for a specific user.
     *
     * @param userId The ID of the user receiving the notification response.
     * @param description The description or content of the notification.
     * @param status The status of the notification response.
     * @return True if the response was successfully sent, false otherwise.
     */
    public boolean sendResponse(String userId, String description, int status) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(SEND_RESOPNSE);
                stm.setString(1, userId);
                stm.setString(2, description);
                stm.setInt(3, status);
                check = stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logError("Exception found on sendResponse() method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }
}
