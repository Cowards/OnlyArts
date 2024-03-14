package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.core.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageDAO {

    private static final DBContext dbContext = DBContext.getInstance();
    private static ImageDAO instance;
    private static final String UPLOAD_IMAGE
            = "INSERT INTO [dbo].[Images]"
            + "([image_id],[image]) VALUES (?,?)";
    private static final String GET_IMAGE
            = "SELECT [image] FROM [dbo].[Images]"
            + "WHERE [image_id] = ?";

    private ImageDAO() {
    }

    public static ImageDAO getInstance() {
        if (instance == null) {
            instance = new ImageDAO();
        }
        return instance;
    }

    public String addImage(String imageData) {
        Connection conn = null;
        PreparedStatement stm = null;
        String imageId = null;
        try {
            conn = dbContext.getConnection();
            stm = conn.prepareStatement(UPLOAD_IMAGE);
            imageId = CodeGenerator.generateUUID(20);
            stm.setString(1, imageId);
            stm.setString(2, imageData);
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ImageDAO.class.getName())
                    .log(Level.SEVERE, "Exception found on uploadImage() method", ex);
        } finally {
            dbContext.closeStatement(stm);
        }
        return imageId;
    }

    public String getImage(String imageId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        String imageData = null;
        try {
            conn = dbContext.getConnection();
            stm = conn.prepareStatement(GET_IMAGE);
            stm.setString(1, imageId);
            rs = stm.executeQuery();
            if (rs.next()) {
                imageData = rs.getString("image");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImageDAO.class.getName())
                    .log(Level.SEVERE, "Exception found on uploadImage() method", ex);
        } finally {
            dbContext.closeResultSet(rs);
            dbContext.closeStatement(stm);
        }
        return imageData;
    }
}
