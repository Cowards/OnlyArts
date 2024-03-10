package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartDAO {

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

    private void logError(String message, Exception ex) {
        Logger.getLogger(UserDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
