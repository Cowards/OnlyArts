package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FavorDAO {

    private static final DBContext context = DBContext.getInstance();
    private static FavorDAO instance;

    private FavorDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(FavorDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static FavorDAO getInstance() {
        if (instance == null) {
            instance = new FavorDAO();
        }
        return instance;
    }
}
