package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowDAO {

    private static final DBContext context = DBContext.getInstance();
    private static FollowDAO instance;

    private void logError(String message, Exception ex) {
        Logger.getLogger(FollowDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    private FollowDAO() {
    }

    public static FollowDAO getInstance() {
        if (instance == null) {
            instance = new FollowDAO();
        }
        return instance;
    }
}
