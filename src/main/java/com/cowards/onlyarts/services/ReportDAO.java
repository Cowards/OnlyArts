package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO {

    private static final DBContext context = DBContext.getInstance();
    private static ReportDAO instance;

    private ReportDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(ReportDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    public static ReportDAO getInstance() {
        if (instance == null) {
            instance = new ReportDAO();
        }
        return instance;
    }

}
