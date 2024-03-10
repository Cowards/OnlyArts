package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtworkDAO {

    private static final DBContext context = DBContext.getInstance();
    private static ArtworkDAO instance;

    private ArtworkDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(ArtworkDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static ArtworkDAO getInstance() {
        if (instance == null) {
            instance = new ArtworkDAO();
        }
        return instance;
    }

}
