package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;

public class ImageDAO {

    private static final DBContext dbContext = DBContext.getInstance();
    private static ImageDAO instance;

    private ImageDAO() {
    }

    public static ImageDAO getInstance() {
        if (instance == null) {
            instance = new ImageDAO();
        }
        return instance;
    }
}
