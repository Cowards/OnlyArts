package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private ReactionDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(ReactionDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static ReactionDAO getInstance() {
        if (instance == null) {
            instance = new ReactionDAO();
        }
        return instance;
    }

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
