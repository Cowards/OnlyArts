package com.cowards.onlyarts.services;

import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.order.OrderERROR;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {

    private static final String GET_ALL
            = "SELECT [order_id], [user_id], "
            + "[status], [payment_method], [order_time], [total_price] "
            + "FROM Orders";
    private static final String GET_TOP_10
            = "SELECT TOP (10) [order_id], [user_id], "
            + "[status], [payment_method], [order_time], [total_price] "
            + "FROM Orders";
    private static final String GET_ALL_BY_USER_ID
            = "SELECT [order_id], [user_id], [status], [payment_method], "
            + "[order_time], [total_price] FROM Orders WHERE user_id = ?";
    private static final String GET_ONE = "SELECT [order_id], [user_id], "
            + "[status], [payment_method], [order_time], [total_price]"
            + " FROM Orders WHERE order_id = ?";
    private static final String CURRENT_MONTH_PROFIT = "SELECT "
            + "(SUM(total_price) * 0.05) "
            + "FROM Orders WHERE YEAR(order_time) = YEAR(GETDATE())"
            + "AND MONTH(order_time) = MONTH(GETDATE())";
    private static final String INSERT = "INSERT INTO Orders "
            + "(order_id, user_id, status, payment_method, total_price) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String GET_ALL_BY_DATE
            = "SELECT [order_id], [user_id], [status], "
            + "[payment_method], [order_time], [total_price] "
            + "FROM Orders "
            + "WHERE DATE(order_time) = ?";
    private static final String GET_ALL_BY_OWNER_ID
            = "SELECT [order_id], [user_id], [status], "
            + "[payment_method], [order_time], [total_price] "
            + "FROM Orders tb1 "
            + "INNER JOIN Order_details tb2 "
            + "ON tb1.order_id = tb2.order_id "
            + "INNER JOIN Artworks tb3 "
            + "ON tb2.artwork_id = tb3.artwork_id "
            + "WHERE owner_id = ?";

    private final DBContext DB = DBContext.getInstance();

    private static OrderDAO instance;

    private OrderDAO() {
    }

    public static OrderDAO getInstance() {
        if (instance == null) {
            instance = new OrderDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, message, ex);
    }

    public List<OrderDTO> getAll() {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<OrderDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL);
            rs = stm.executeQuery();
            while (rs.next()) {
                OrderDTO ordersDTO = new OrderDTO();
                ordersDTO.setOrderId(rs.getString(1));
                ordersDTO.setUserId(rs.getString(2));
                ordersDTO.setStatus(rs.getInt(3));
                ordersDTO.setPaymentMethod(rs.getString(4));
                ordersDTO.setOrderTime(rs.getDate(5));
                ordersDTO.setTotalPrice(rs.getFloat(6));
                list.add(ordersDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    public float currentMonthProfit() throws OrderERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        float profit = 0;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(CURRENT_MONTH_PROFIT);
            rs = stm.executeQuery();
            if (rs.next()) {
                profit = rs.getFloat(1);
            } else {
                throw new OrderERROR("Cannot get current month profit");
            }
        } catch (SQLException e) {
            logError("Exception found on currentMonthProfit() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return profit;
    }

    public boolean insert(OrderDTO ordersDTO) throws OrderERROR {
        Connection conn = null;
        boolean check = false;
        PreparedStatement stm = null;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(INSERT);
            stm.setString(1, ordersDTO.getOrderId());
            stm.setString(2, ordersDTO.getUserId());
            stm.setInt(3, ordersDTO.getStatus());
            stm.setString(4, ordersDTO.getPaymentMethod());
            stm.setFloat(5, ordersDTO.getTotalPrice());
            if (stm.executeUpdate() > 0) {
                check = true;
            } else {
                throw new OrderERROR("Cannot insert new order");
            }
        } catch (SQLException e) {
            logError("Exception found on insert(OrderDTO ordersDTO) method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    public OrderDTO getOne(String orderId) throws OrderERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        OrderDTO ordersDTO = null;
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ONE);
            stm.setString(1, orderId);
            rs = stm.executeQuery();
            ordersDTO = new OrderDTO();
            if (rs.next()) {
                ordersDTO.setOrderId(rs.getString(1));
                ordersDTO.setUserId(rs.getString(2));
                ordersDTO.setStatus(rs.getInt(3));
                ordersDTO.setPaymentMethod(rs.getString(4));
                ordersDTO.setOrderTime(rs.getDate(5));
                ordersDTO.setTotalPrice(rs.getFloat(6));
            } else {
                throw new OrderERROR("This id order does not exist");
            }
        } catch (SQLException e) {
            logError("Exception found on getOne(String orderId) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return ordersDTO;
    }

    public List<OrderDTO> getAll(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<OrderDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_USER_ID);
            stm.setString(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                OrderDTO ordersDTO = new OrderDTO();
                ordersDTO.setOrderId(rs.getString(1));
                ordersDTO.setUserId(rs.getString(2));
                ordersDTO.setStatus(rs.getInt(3));
                ordersDTO.setPaymentMethod(rs.getString(4));
                ordersDTO.setOrderTime(rs.getDate(5));
                ordersDTO.setTotalPrice(rs.getFloat(6));
                list.add(ordersDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll(String userId) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    public List<OrderDTO> getAll(Date orderTime) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        OrderDTO orderDTO = new OrderDTO();
        List<OrderDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_DATE);
            stm.setDate(1, orderTime);
            rs = stm.executeQuery();
            while (rs.next()) {
                orderDTO.setOrderId(rs.getString(1));
                orderDTO.setUserId(rs.getString(2));
                orderDTO.setStatus(rs.getInt(3));
                orderDTO.setPaymentMethod(rs.getString(4));
                orderDTO.setOrderTime(rs.getDate(5));
                orderDTO.setTotalPrice(rs.getFloat(6));
                list.add(orderDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll(Date orderTime) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    public List<OrderDTO> getAllByOwnerId(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        OrderDTO orderDTO = new OrderDTO();
        List<OrderDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_ALL_BY_OWNER_ID);
            stm.setString(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                orderDTO.setOrderId(rs.getString(1));
                orderDTO.setUserId(rs.getString(2));
                orderDTO.setStatus(rs.getInt(3));
                orderDTO.setPaymentMethod(rs.getString(4));
                orderDTO.setOrderTime(rs.getDate(5));
                orderDTO.setTotalPrice(rs.getFloat(6));
                list.add(orderDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on "
                    + "getAllByOwnerId(String userId) method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    public List<OrderDTO> getTop10() {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<OrderDTO> list = new ArrayList<>();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_TOP_10);
            rs = stm.executeQuery();
            while (rs.next()) {
                OrderDTO ordersDTO = new OrderDTO();
                ordersDTO.setOrderId(rs.getString(1));
                ordersDTO.setUserId(rs.getString(2));
                ordersDTO.setStatus(rs.getInt(3));
                ordersDTO.setPaymentMethod(rs.getString(4));
                ordersDTO.setOrderTime(rs.getDate(5));
                ordersDTO.setTotalPrice(rs.getFloat(6));
                list.add(ordersDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAll() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }
}
