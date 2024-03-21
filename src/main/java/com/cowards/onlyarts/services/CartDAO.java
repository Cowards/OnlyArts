package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.cart.CartDTO;
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
 * This class provides data access methods for interacting with user's cart data in the database.
 * It allows retrieving, inserting, and deleting artworks from the user's cart.
 */
public class CartDAO {

    private static final String GET_ALL_CART_BY_USER_ID
            = "SELECT [user_id], [artwork_id] FROM Carts WHERE user_id = ?";
    private static final String INSERT_CART
            = "INSERT INTO Carts ([user_id], [artwork_id]) VALUES(?, ?)";
    private static final String DELETE_CART
            = "DELETE FROM Carts WHERE artwork_id = ? AND user_id = ?";
    private static final String DELETE_ALL_BY_USER_ID
            = "DELETE FROM Carts WHERE user_id = ?";

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

    /**
     * Retrieves all artworks in the cart for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of CartDTO objects representing the artworks in the user's cart.
     */
    public List<CartDTO> getAll(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<CartDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_CART_BY_USER_ID);
            stm.setString(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                CartDTO cartDTO = new CartDTO();
                cartDTO.setUserId(rs.getString(1));
                cartDTO.setArtworkId(rs.getString(2));
                list.add(cartDTO);
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
     * @param cartDTO the CartDTO object representing the artwork to be inserted into the cart.
     * @return true if the artwork is successfully inserted into the cart, false otherwise.
     */
    public boolean insert(CartDTO cartDTO) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(INSERT_CART);
            stm.setString(1, cartDTO.getUserId());
            stm.setString(2, cartDTO.getArtworkId());
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new CartERROR("Cannot add this artwork to cart");
            }
        } catch (CartERROR | SQLException e) {
            logError("Exception found on insert(CartrDTO cartDTO) method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Deletes an artwork from the user's cart.
     *
     * @param cartDTO the CartDTO object representing the artwork to be deleted from the cart.
     * @return true if the artwork is successfully deleted from the cart, false otherwise.
     */
    public boolean delete(CartDTO cartDTO) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(DELETE_CART);
            stm.setString(1, cartDTO.getArtworkId());
            stm.setString(2, cartDTO.getUserId());
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new CartERROR("Cannot delete this artwork from cart");
            }
        } catch (CartERROR | SQLException e) {
            logError("Exception found on delete(CartDTO cartDTO) method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Deletes all artworks from the cart for a specific user.
     *
     * @param userId the ID of the user whose cart needs to be cleared.
     * @return true if all artworks are successfully deleted from the cart, false otherwise.
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
        } catch (CartERROR | SQLException e) {
            logError("Exception found on delete(String userId) method", e);
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
