package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDAO {

    private static final DBContext context = DBContext.getInstance();
    private static CommentDAO instance;

    private CommentDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(CommentDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static CommentDAO getInstance() {
        if (instance == null) {
            instance = new CommentDAO();
        }
        return instance;
    }
}
