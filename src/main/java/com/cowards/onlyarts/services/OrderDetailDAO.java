package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailDTO;
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
 * This class provides data access operations for managing order details in the database.
 */
public class OrderDetailDAO {

    private static final String INSERT
            = "INSERT INTO Order_details "
            + "(order_id, artwork_id) "
            + "VALUES (?, ?)";
    private static final String GET_ALL_BY_ORDER_ID
            = "SELECT order_id, artwork_id "
            + "FROM Order_details "
            + "WHERE order_id = ?";

    private static final DBContext context = DBContext.getInstance();

    private static OrderDetailDAO instance = null;

    /**
     * Private constructor to prevent direct instantiation of the OrderDetailDAO class.
     */
    private OrderDetailDAO() {
    }

    /**
     * Retrieves an instance of the OrderDetailDAO class. If no instance
     * exists, a new instance is created and returned.
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
     * @param orderDetailDTO The OrderDetailDTO object representing the order detail to be inserted.
     * @return True if the order detail was successfully inserted, otherwise false.
     * @throws OrderDetailERROR If an error occurs while inserting the order detail.
     */
    public boolean insert(OrderDetailDTO orderDetailDTO) throws OrderDetailERROR {
        Connection conn = null;
        boolean check = false;
        PreparedStatement stm = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(INSERT);
            stm.setString(1, orderDetailDTO.getOrderId());
            stm.setString(2, orderDetailDTO.getArtworkId());
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new OrderDetailERROR("Cannot insert new order detail");
            }
        } catch (SQLException e) {
            logError("Exception found on insert(OrderDetailsDTO orderDetailsDTO) method", e);
        } finally {
            context.closeStatement(stm);
        }
        return check;
    }

    /**
     * Retrieves all order details associated with a specific order.
     * 
     * @param orderId The ID of the order whose details are to be retrieved.
     * @return A list of OrderDetailDTO objects representing the order details.
     */
    public List<OrderDetailDTO> getAll(String orderId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        List<OrderDetailDTO> list = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_ORDER_ID);
            stm.setString(1, orderId);
            rs = stm.executeQuery();
            while (rs.next()) {
                orderDetailDTO.setOrderId(rs.getString(1));
                orderDetailDTO.setArtworkId(rs.getString(2));
                list.add(orderDetailDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll(String orderId) method", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }
}
