package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access operations for managing tokens.
 */
public class TokenDAO {

    private static TokenDAO instance = null;
    private static final DBContext context = DBContext.getInstance();
    private static final String GET_TOKEN = "SELECT [user_id],[token],[validDate],[expiredDate],[status]"
            + "FROM [dbo].[Tokens] WHERE [token] = ?";
    private static final String ADD_TOKEN = "INSERT INTO [dbo].[Tokens]"
            + "([user_id],[token],[validDate],[expiredDate],[status]) VALUES (?, ?, ?, ?,?)";
    private static final String DEACTIVATE_TOKEN = "UPDATE [dbo].[Tokens] "
            + "SET [status] = ? "
            + "WHERE [token] = ?";
    private static final String DELETE_TOKEN
            = "DELETE FROM [dbo].[Tokens]"
            + "WHERE [token] = ?";

    /**
     * Private constructor for the TokenDAO class. Prevents instantiation from
     * outside the class.
     */
    private TokenDAO() {
    }

    private void logError(String message, Exception ex) {
        Logger.getLogger(TokenDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    /**
     * Gets the instance of TokenDAO using the singleton pattern. If the
     * instance is null, a new instance is created.
     *
     * @return The instance of TokenDAO.
     */
    public static TokenDAO getInstance() {
        if (instance == null) {
            instance = new TokenDAO();
        }
        return instance;
    }

    /**
     * Retrieves a token by its token string.
     *
     * @param tokenString The token string to retrieve.
     * @return The TokenDTO object representing the token.
     * @throws TokenERROR if the token string is invalid or not found in the
     * system.
     */
    public TokenDTO getToken(String tokenString) throws TokenERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        TokenDTO token = null;

        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_TOKEN);
            stm.setString(1, tokenString);
            rs = stm.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("user_id");
                Date validDate = rs.getDate("validDate");
                Date expiredDate = rs.getDate("expiredDate");
                int status = rs.getInt("status");
                token = new TokenDTO(userId, tokenString, validDate, expiredDate, status);
            } else {
                throw new TokenERROR("Invalid token string");
            }

            context.closeResultSet(rs);
            context.closeStatement(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TokenDAO.class.getName()).log(Level.SEVERE,
                    "Exception found on getToken() method ", ex);
        }
        return token;
    }

    /**
     * Adds a reset password token for the specified user.
     *
     * @param userId The user ID for which the token is generated.
     * @return The generated token string.
     */
    public String addResetPasswordToken(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        String tokenString = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_TOKEN);
            tokenString = CodeGenerator.generateRandomToken(32);
            Date validDate = new Date(System.currentTimeMillis());
            Date expiredDate = new Date(System.currentTimeMillis() + 900_000L);
            stm.setString(1, userId);
            stm.setString(2, tokenString);
            stm.setDate(3, validDate);
            stm.setDate(4, expiredDate);
            stm.setInt(5, 0b011);
            stm.executeUpdate();

            context.closeStatement(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TokenDAO.class.getName()).log(Level.SEVERE,
                    "Exception founf on addResetPasswordToken method", ex);
        }
        return tokenString;
    }

    /**
     * Adds a login token for the specified user.
     *
     * @param userId The user ID for which the token is generated.
     * @return The generated token string.
     */
    public TokenDTO addLoginToken(String userId) {
        Connection conn = null;
        PreparedStatement stm = null;
        TokenDTO token = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_TOKEN);
            String tokenString = CodeGenerator.generateRandomToken(32);
            Date validDate = new Date(System.currentTimeMillis());
            Date expiredDate = new Date(System.currentTimeMillis() + 2_592_000_000L);
            stm.setString(1, userId);
            stm.setString(2, tokenString);
            stm.setDate(3, validDate);
            stm.setDate(4, expiredDate);
            stm.setInt(5, 0b101);
            stm.executeUpdate();
            token = new TokenDTO(userId, tokenString, validDate, expiredDate, 0b101);
            context.closeStatement(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TokenDAO.class.getName()).log(Level.SEVERE,
                    "Exception founf on addResetPasswordToken method", ex);
        }
        return token;
    }

    /**
     * Deactivates a token in the system.
     *
     * @param tokenString The token string to deactivate.
     * @return True if the token is deactivated successfully, otherwise false.
     * @throws TokenERROR if the token string is invalid or not found in the
     * system.
     */
    public boolean deactivateToken(String tokenString)
            throws TokenERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = context.getConnection();
            TokenDTO token = this.getToken(tokenString);
            stm = conn.prepareStatement(DEACTIVATE_TOKEN);
            int status = token.isValid() ? token.getStatus() ^ 1 : token.getStatus();
            stm.setInt(1, status);
            stm.setString(2, tokenString);
            stm.executeUpdate();

            context.closeStatement(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TokenDAO.class.getName()).log(Level.SEVERE,
                    "Exception found on deactivateToken method", ex);
        }
        return true;
    }

    public boolean removeToken(String tokenString) {
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(DELETE_TOKEN);
            stm.setString(1, tokenString);
            stm.executeUpdate();

            context.closeStatement(stm);
        } catch (SQLException ex) {
            Logger.getLogger(TokenDAO.class.getName()).log(Level.SEVERE,
                    "Exception found on deactivateToken method", ex);
        }
        return true;
    }
}
