package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailERROR;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access operations for managing order details in the
 * database.
 */
public class OrderDetailDAO {

    private static final String INSERT
            = "INSERT INTO Order_details "
            + "(order_id, artwork_id, price) "
            + "VALUES (?, ?,?)";
    private static final String GET_ALL_BY_ORDER_ID
            = "SELECT a.[artwork_id], a.[owner_id], a.[cate_id], a.[name]"
            + ", a.[description], a.[artwork_image], a.[price]"
            + ", a.[released_date], a.[status] "
            + "FROM [dbo].[Order_details] od "
            + "LEFT JOIN [dbo].[Artworks] a "
            + "ON od.[artwork_id] = a.[artwork_id] "
            + "WHERE od.[order_id] = ?";
    private static final String GET_USER_ARTWORK
            = "SELECT * FROM [dbo].[Orders] o "
            + "LEFT JOIN [dbo].[Order_details] od "
            + "ON o.[order_id] = od.[order_id] "
            + "WHERE o.[user_id] = ? "
            + "AND od.[artwork_id] = ?";
    private static final String GET_ARTWORKS_PURCHASED
            = "SELECT a.[artwork_id], a.[owner_id], a.[cate_id], a.[name], "
            + "a.[description], a.[artwork_image], a.[price], a.[released_date], "
            + "a.[status] "
            + "FROM [dbo].[Orders] o "
            + "LEFT JOIN [dbo].[Order_details] od "
            + "ON o.[order_id] = od.[order_id] "
            + "LEFT JOIN [dbo].[Artworks] a "
            + "ON a.[artwork_id] = od.[artwork_id]"
            + "WHERE o.[user_id] = ? ";

    private static final DBContext context = DBContext.getInstance();

    private static OrderDetailDAO instance = null;

    /**
     * Private constructor to prevent direct instantiation of the OrderDetailDAO
     * class.
     */
    private OrderDetailDAO() {
    }

    /**
     * Retrieves an instance of the OrderDetailDAO class. If no instance exists,
     * a new instance is created and returned.
     *
     * @return An instance of the OrderDetailDAO class.
     */
    public static OrderDetailDAO getInstance() {
        if (instance == null) {
            instance = new OrderDetailDAO();
        }
        return instance;
    }

    /**
     * Logs an error message along with the exception stack trace.
     *
     * @param message The error message to be logged.
     * @param ex The Throwable object representing the exception.
     */
    private void logError(String message, Exception ex) {
        Logger.getLogger(OrderDetailDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    /**
     * Inserts a new order detail into the database.
     *
     * @param orderId The ID of the order.
     * @param artworkId The ID of the artwork.
     * @return True if the order detail was successfully inserted, otherwise
     * false.
     * @throws OrderDetailERROR If an error occurs while inserting the order
     * detail.
     */
    public boolean insert(String orderId, String artworkId, double price) throws OrderDetailERROR {
        Connection conn = null;
        boolean check = false;
        PreparedStatement stm = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(INSERT);
            stm.setString(1, orderId);
            stm.setString(2, artworkId);
            stm.setDouble(3, price);
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new OrderDetailERROR("Cannot insert new order detail");
            }
        } catch (SQLException e) {
            logError("Exception found on insert() method", e);
        } finally {
            context.closeStatement(stm);
        }
        return check;
    }

    /**
     * Retrieves all order details associated with a specific order.
     *
     * @param orderId The ID of the order whose details are to be retrieved.
     * @return A list of ArtworkDTO objects representing the order details.
     */
    public List<ArtworkDTO> getAll(String orderId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<ArtworkDTO> list = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_ORDER_ID);
            stm.setString(1, orderId);
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
            logError("Exception found on getAll() method", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }

    public boolean isBuy(String userId, String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean check = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USER_ARTWORK);
            stm.setString(1, userId);
            stm.setString(2, artworkId);
            rs = stm.executeQuery();
            if (rs.next()) {
                check = true;
            }
        } catch (SQLException ex) {
            logError("Exception found on isBuy() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return check;
    }

    public List<ArtworkDTO> isBuy(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<ArtworkDTO> list = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_ARTWORKS_PURCHASED);
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
        } catch (SQLException ex) {
            logError("Exception found on isBuy() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }
}
