package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestDAO {

    private static RequestDAO instance;
    private static final DBContext DB = DBContext.getInstance();

    public static RequestDAO getInstance() {
        if (instance == null) {
            instance = new RequestDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(UserDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

}
