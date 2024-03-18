/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author ngocn
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

    public static CartDAO getInstance() {
        if (instance == null) {
            instance = new CartDAO();
        }
        return instance;
    }

    public List<CartDTO> getAll(String user_id) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<CartDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_CART_BY_USER_ID);
            stm.setString(1, user_id);
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
