package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO {

    private static CategoryDAO instance = null;
    private static final DBContext DB = DBContext.getInstance();

    private CategoryDAO() {
    }

    public static CategoryDAO getInstance() {
        if (instance == null) {
            instance = new CategoryDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, message, ex);
    }
}
