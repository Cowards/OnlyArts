package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReactionDAO {

    private static final DBContext context = DBContext.getInstance();
    private static ReactionDAO instance;

    private ReactionDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(ReactionDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static ReactionDAO getInstance() {
        if (instance == null) {
            instance = new ReactionDAO();
        }
        return instance;
    }
}
