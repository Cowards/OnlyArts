package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.category.CategoryDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access methods for interacting with categories in
 * the database.
 */
public class CategoryDAO {

    private static final String GET_CATEGORY_BY_NAME
            = "SELECT [cate_id], [cate_name] FROM [dbo].[Categories] "
            + "WHERE [cate_name] = ?";
    private static final String GET_CATEGORY_BY_ID
            = "SELECT [cate_id], [cate_name] FROM [dbo].[Categories] "
            + "WHERE [cate_id] = ?";

    private static CategoryDAO instance = null;
    private static final DBContext DB = DBContext.getInstance();

    private CategoryDAO() {
    }

    /**
     * Gets the instance of CategoryDAO.
     *
     * @return the instance of CategoryDAO.
     */
    public static CategoryDAO getInstance() {
        if (instance == null) {
            instance = new CategoryDAO();
        }
        return instance;
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, message, ex);
    }

    public CategoryDTO getCateByName(String cateName) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        CategoryDTO categoryDTO = new CategoryDTO();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_CATEGORY_BY_NAME);
            stm.setString(1, cateName);
            rs = stm.executeQuery();
            while (rs.next()) {
                categoryDTO.setCateId(rs.getString(1));
                categoryDTO.setCateName(rs.getString(2));
            }
        } catch (SQLException e) {
            logError("Exception found on getCateByName() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return categoryDTO;
    }

    public CategoryDTO getCateById(String cateId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        CategoryDTO categoryDTO = new CategoryDTO();
        try {
            conn = DB.getConnection();
            stm = conn.prepareStatement(GET_CATEGORY_BY_ID);
            stm.setString(1, cateId);
            rs = stm.executeQuery();
            while (rs.next()) {
                categoryDTO.setCateId(rs.getString(1));
                categoryDTO.setCateName(rs.getString(2));
            }
        } catch (SQLException e) {
            logError("Exception found on getCateByName() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return categoryDTO;
    }
}
