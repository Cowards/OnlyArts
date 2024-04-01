package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.cart.CartERROR;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access methods for interacting with user's cart data
 * in the database. It allows retrieving, inserting, and deleting artworks from
 * the user's cart.
 */
public class CartDAO {

    private static final String GET_ALL_BY_USER_ID
            = "SELECT a.[artwork_id], a.[owner_id], a.[cate_id], a.[name]"
            + ", a.[description], a.[artwork_image], a.[price]"
            + ", a.[released_date], a.[status] "
            + "FROM [dbo].[Carts] c "
            + "LEFT JOIN [dbo].[Artworks] a "
            + "ON c.[artwork_id] = a.[artwork_id] "
            + "WHERE c.[user_id] = ?";
    private static final String INSERT_CART
            = "INSERT INTO Carts ([user_id], [artwork_id]) VALUES(?, ?)";
    private static final String DELETE_CART
            = "DELETE FROM Carts WHERE artwork_id = ? AND user_id = ?";
    private static final String DELETE_ALL_BY_USER_ID
            = "DELETE FROM Carts WHERE user_id = ?";
    private static final String CHECK_ADDED
            = "SELECT [user_id], [artwork_id] FROM Carts "
            + "WHERE [user_id] = ? AND [artwork_id] = ?";

    private static final DBContext DB = DBContext.getInstance();

    private static CartDAO instance = null;

    private CartDAO() {
    }

    /**
     * Gets the instance of CartDAO.
     *
     * @return the instance of CartDAO.
     */
    public static CartDAO getInstance() {
        if (instance == null) {
            instance = new CartDAO();
        }
        return instance;
    }

    public boolean checkAdded(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_USER_ID);
            stm.setString(1, userId);
            rs = stm.executeQuery();
            check = rs.next();
        } catch (SQLException e) {
            logError("Exception found on getAll(String user_id) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Retrieves all artworks in the cart for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of CartDTO objects representing the artworks in the user's
     * cart.
     */
    public List<ArtworkDTO> getAll(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<ArtworkDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_USER_ID);
            stm.setString(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                ArtworkDTO artworkDTO
                        = new ArtworkDTO(rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getFloat(7),
                                rs.getDate(8),
                                rs.getInt(9));
                list.add(artworkDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll(String user_id) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    /**
     * Inserts an artwork into the user's cart.
     *
     * @param userId
     * @param artworkId
     * @return true if the artwork is successfully inserted into the cart, false
     * otherwise.
     */
    public boolean insert(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(INSERT_CART);
            stm.setString(1, userId);
            stm.setString(2, artworkId);
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new CartERROR("Cannot add this artwork to cart");
            }
        } catch (CartERROR | SQLException ex) {
            logError("Exception found on insert() method", ex);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Deletes an artwork from the user's cart.
     *
     * @param userId
     * @param artworkId
     * @return true if the artwork is successfully deleted from the cart, false
     * otherwise.
     */
    public boolean delete(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(DELETE_CART);
            stm.setString(1, artworkId);
            stm.setString(2, userId);
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new CartERROR("Cannot delete this artwork from cart");
            }
        } catch (CartERROR | SQLException ex) {
            logError("Exception found on delete(String userId, String artworkId) method", ex);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Deletes all artworks from the cart for a specific user.
     *
     * @param userId the ID of the user whose cart needs to be cleared.
     * @return true if all artworks are successfully deleted from the cart,
     * false otherwise.
     */
    public boolean delete(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(DELETE_ALL_BY_USER_ID);
            stm.setString(1, userId);
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new CartERROR("Cannot delete these cart");
            }
        } catch (CartERROR | SQLException ex) {
            logError("Exception found on delete(String userId) method", ex);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(UserDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
