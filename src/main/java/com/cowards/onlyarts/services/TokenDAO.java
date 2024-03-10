package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenDAO {

    private static TokenDAO instance = null;
    private static final DBContext context = DBContext.getInstance();

    private TokenDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(TokenDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static TokenDAO getInstance() {
        if (instance == null) {
            instance = new TokenDAO();
        }
        return instance;
    }

}
