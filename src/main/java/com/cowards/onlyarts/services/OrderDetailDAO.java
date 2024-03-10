package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDetailDAO {

    private static final DBContext context = DBContext.getInstance();
    private static OrderDetailDAO instance = null;

    private OrderDetailDAO() {
    }

    public static OrderDetailDAO getInstance() {
        if (instance == null) {
            instance = new OrderDetailDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(OrderDetailDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
