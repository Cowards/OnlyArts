package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.comment.CommentDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access methods for interacting with comments in the database.
 */
public class CommentDAO {

    private static final DBContext context = DBContext.getInstance();
    private static CommentDAO instance;
    private static final String GET_COMMENTS
            = "SELECT [comment_id],[artwork_id],[commenter_id],"
            + "       [description], [comment_time]"
            + "FROM [dbo].[Comments]";
    private static final String ADD_COMMENT
            = "INSERT INTO [dbo].[Comments]"
            + "([comment_id],[artwork_id],[commenter_id],"
            + "[description])"
            + " VALUES (?,?,?,?)";
    private static final String REMOVE_COMMENT
            = "DELETE FROM [dbo].[Comments]"
            + "WHERE [comment_id] = ?";

    private CommentDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(CommentDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    /**
     * Gets the instance of CommentDAO.
     *
     * @return the instance of CommentDAO.
     */
    public static CommentDAO getInstance() {
        if (instance == null) {
            instance = new CommentDAO();
        }
        return instance;
    }

    /**
     * Retrieves comments for a specific artwork.
     *
     * @param artworkId the ID of the artwork.
     * @return a list of CommentDTO objects representing the comments for the artwork.
     */
    public List<CommentDTO> getArtworkComment(String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<CommentDTO> comments = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_COMMENTS + " WHERE [artwork_id] = ?");
            stm.setString(1, artworkId);
            rs = stm.executeQuery();
            CommentDTO comment = null;
            while (rs.next()) {
                comment = new CommentDTO();
                comment.setCommentId(rs.getString("comment_id"));
                comment.setArtworkId(rs.getString("artwork_id"));
                comment.setCommenterId(rs.getString("commenter_id"));
                comment.setDescription(rs.getString("description"));
                comment.setComment_time(rs.getDate("comment_time"));
                comments.add(comment);
            }
        } catch (SQLException ex) {
            logError("Exception found on getArtworkComment() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return comments;
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment.
     * @return a CommentDTO object representing the retrieved comment.
     */
    public CommentDTO getComment(String commentId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        CommentDTO comment = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_COMMENTS + " WHERE [comment_id] = ?");
            stm.setString(1, commentId);
            rs = stm.executeQuery();
            if (rs.next()) {
                comment = new CommentDTO();
                comment.setCommentId(commentId);
                comment.setArtworkId(rs.getString("artwork_id"));
                comment.setCommenterId(rs.getString("commenter_id"));
                comment.setDescription(rs.getString("description"));
                comment.setComment_time(rs.getDate("comment_time"));
            }
        } catch (SQLException ex) {
            logError("Exception found on getComment() method", ex);
        } finally {
            context.closeStatement(stm);
        }
        return comment;
    }

    /**
     * Adds a new comment to the database.
     *
     * @param comment the CommentDTO object representing the comment to be added.
     * @return true if the comment is successfully added, false otherwise.
     */
    public boolean addComment(CommentDTO comment) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_COMMENT);
            stm.setString(1, comment.getCommentId());
            stm.setString(2, comment.getArtworkId());
            stm.setString(3, comment.getCommenterId());
            stm.setString(4, comment.getDescription());
            res = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on addComment() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

    /**
     * Removes a comment from the database.
     *
     * @param commentId the ID of the comment to be removed.
     * @return true if the comment is successfully removed, false otherwise.
     */
    public boolean removeComment(String commentId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(REMOVE_COMMENT);
            stm.setString(1, commentId);
            res = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on addComment() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

}
