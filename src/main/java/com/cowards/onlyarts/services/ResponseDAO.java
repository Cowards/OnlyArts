package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseDAO {

    private static ResponseDAO instance;
    private static final DBContext DB = DBContext.getInstance();

    private ResponseDAO() {
    }

    public static ResponseDAO getInstance() {
        if (instance == null) {
            instance = new ResponseDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(ResponseDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }
}
