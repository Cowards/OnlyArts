package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final DBContext context = DBContext.getInstance();
    private static UserDAO instance;
    private static final String GET_USERS
            = "SELECT [user_id]"
            + ",[role_id],[first_name],[last_name],[phone],[email]"
            + ",[address],[avatar],[join_date],[bio],[status],[password]"
            + " FROM [dbo].[Users]";
    private static final String CHANGE_STATUS
            = "UPDATE [Users] "
            + "SET [status] = ? "
            + "WHERE [user_id] = ?";
    private static final String ADD_USER
            = "INSERT INTO [dbo].[Users]"
            + "([user_id],[role_id],[first_name],[last_name],[phone]"
            + "[email],[address],[join_date],[bio],[status],[password])"
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
     private static final String UPDATE_USER_INFO
            = "UPDATE [dbo].[Users] SET "
            + "[first_name] = ?,"
            + "[last_name] =? ,"
            + "[phone] = ?,"
            + "[email] = ?,"
            + "[address] = ?,"
            + "[bio] = ? "
            + "WHERE [user_id] = ? ";
     private static final String CHANGE_PASSWORD
            = "UPDATE [dbo].[Users]"
            + "SET [password] = ?"
            + "WHERE [user_id] = ?";

    private void logError(String message, Exception ex) {
        Logger.getLogger(UserDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public UserDTO addNewUser(UserDTO user) {
        Connection conn = null;
        PreparedStatement stm = null;
        String userId = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_USER);
            userId = CodeGenerator.generateUUID(20);
            stm.setString(1, userId);
            stm.setString(2, user.getRoleId());
            stm.setString(3, user.getFirstName());
            stm.setString(4, user.getLastName());
            stm.setString(5, user.getPhone());
            stm.setString(6, user.getEmail());
            stm.setString(7, user.getAddress());
            stm.setDate(9, new Date(System.currentTimeMillis()));
            stm.setString(10, user.getBio());
            stm.setInt(11, 0b000);
            stm.setString(12, user.getPassword());
            stm.executeUpdate();
            user.setUserId(userId);
        } catch (SQLException ex) {
            logError("Exception found on addNewUser() method", ex);
        }
        return user;
    }

    public UserDTO getUserByEmail(String email) throws UserERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        UserDTO user = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USERS + " WHERE [email] = ?");
            stm.setString(1, email);
            rs = stm.executeQuery();
            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getString("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setRoleId(rs.getString("role_id"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setJoinDate(rs.getDate("join_date"));
                user.setBio(rs.getString("bio"));
                user.setStatus(rs.getString("status").equals("Active") ? 1 : 0);
                user.setAvatar(rs.getString("avatar"));
                user.setPassword(rs.getString("password"));
            } else {
                throw new UserERROR("This email does not exist in the system");
            }
        } catch (SQLException ex) {
            logError("Exception found on getUserByEmail() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
            context.closeConnection(conn);
        }
        return user;
    }

    public UserDTO getUserById(String userId) throws UserERROR {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        UserDTO user = null;

        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USERS + " WHERE [user_id] = ?");
            stm.setString(1, userId);
            rs = stm.executeQuery();
            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getString("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setRoleId(rs.getString("role_id"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setJoinDate(rs.getDate("join_date"));
                user.setBio(rs.getString("bio"));
                user.setStatus(rs.getInt("status"));
                user.setAvatar(rs.getString("avatar"));
                user.setPassword(rs.getString("password"));
            } else {
                throw new UserERROR("This id does not exist in the system");
            }
        } catch (SQLException ex) {
            logError("Exception found on getUserById() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
            context.closeConnection(conn);
        }
        return user;
    }

    public boolean changeStatus(String userId, int status, int state) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(CHANGE_STATUS);
            stm.setInt(1, (status ^ state));
            stm.setString(2, userId);
            stm.executeUpdate();
            res = true;
        } catch (SQLException ex) {
            logError("Exception found on changeStatus() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
            context.closeConnection(conn);
        }
        return res;
    }
    
    public boolean updateUserInfo(UserDTO user) {
        boolean check = false;
        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = context.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(UPDATE_USER_INFO);
                stm.setString(1, user.getFirstName());
                stm.setString(2, user.getLastName());
                stm.setString(3, user.getPhone());
                stm.setString(4, user.getEmail());
                stm.setString(5, user.getAddress());
                stm.setString(6, user.getBio());
                stm.setString(7, user.getUserId());
                check = stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logError("Exception found on updateUser() method", e);
            check = false;
        } finally {
            context.closeStatement(stm);
        }
        return check;
    }

    public boolean changePassword(String userId, String newPw) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(CHANGE_PASSWORD);
            stm.setString(1, newPw);
            stm.setString(2, userId);
            stm.executeUpdate();
            res = true;
        } catch (SQLException ex) {
            logError("Exception found on changePassword() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }
    
}
