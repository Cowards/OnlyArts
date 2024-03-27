package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access operations for managing reactions to artworks
 * in the database.
 */
public class ReactionDAO {

    private static final DBContext context = DBContext.getInstance();
    private static ReactionDAO instance;
    private static final String ADD_REACTION
            = "INSERT INTO [dbo].[Artwork_reactions]"
            + "([artwork_id] , [user_id]) VALUES (? , ?)";
    private static final String REMOVE_REACTION
            = "DELETE FROM [dbo].[Artwork_reactions]"
            + "WHERE [artwork_id] = ? AND [user_id] = ?";
    private static final String COUNT_REACTION
            = "SELECT count([user_id]) as [count] "
            + "FROM [dbo].[Artwork_reactions]"
            + "WHERE [artwork_id] = ?";
    private static final String CHECK_REACTION
            = "SELECT [user_id],[artwork_id]"
            + "FROM [dbo].[Artwork_reactions]"
            + "WHERE [artwork_id] = ? AND [user_id] = ?";

    /**
     * Private constructor to prevent direct instantiation of the ReactionDAO
     * class.
     */
    private ReactionDAO() {
    }

    /**
     * Logs an error message along with the exception stack trace.
     *
     * @param message The error message to be logged.
     * @param ex The Throwable object representing the exception.
     */
    private void logError(String message, Exception ex) {
        Logger.getLogger(ReactionDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    /**
     * Retrieves an instance of the ReactionDAO class. If no instance exists, a
     * new instance is created and returned.
     *
     * @return An instance of the ReactionDAO class.
     */
    public static ReactionDAO getInstance() {
        if (instance == null) {
            instance = new ReactionDAO();
        }
        return instance;
    }

    public boolean checkReaction(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(CHECK_REACTION);
            stm.setString(1, artworkId);
            stm.setString(2, userId);
            rs = stm.executeQuery();
            res = rs.next();
        } catch (SQLException ex) {
            logError("Exception found on addReaction() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

    /**
     * Adds a reaction to an artwork.
     *
     * @param userId The ID of the user who reacted to the artwork.
     * @param artworkId The ID of the artwork to which the reaction is added.
     * @return True if the reaction was successfully added, otherwise false.
     */
    public boolean addReaction(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_REACTION);
            stm.setString(1, artworkId);
            stm.setString(2, userId);
            res = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on addReaction() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

    /**
     * Removes a reaction from an artwork.
     *
     * @param userId The ID of the user whose reaction is to be removed.
     * @param artworkId The ID of the artwork from which the reaction is
     * removed.
     * @return True if the reaction was successfully removed, otherwise false.
     */
    public boolean removeReaction(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(REMOVE_REACTION);
            stm.setString(1, artworkId);
            stm.setString(2, userId);
            res = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on removeReaction() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

    /**
     * Counts the number of reactions for a specific artwork.
     *
     * @param artworkId The ID of the artwork for which the reactions are
     * counted.
     * @return The number of reactions for the artwork.
     */
    public int countReaction(String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(COUNT_REACTION);
            stm.setString(1, artworkId);
            rs = stm.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException ex) {
            logError("Exception found on countReaction() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return count;
    }
}
