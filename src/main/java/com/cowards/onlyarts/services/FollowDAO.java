package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.user.UserDTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowDAO {

    private static final DBContext context = DBContext.getInstance();
    private static FollowDAO instance;
    private static final String ADD_FOLLOW
            = "INSERT INTO [dbo].[Followings]"
            + "([user_id],[followed_user_id]) VALUES (?,?)";
    private static final String UNFOLLOW
            = "DELETE FROM [dbo].[Followings]"
            + "WHERE [user_id] = ? AND [followed_user_id] = ?";
    private static final String GET_FOLLOWING = "SELECT tb2.[user_id],[role_id],"
            + "[first_name],[last_name],[avatar],[phone],[email],[address],"
            + "[join_date],[bio],[status],[password] "
            + "FROM [OnlyArts].[dbo].[Followings] tb1 "
            + "RIGHT JOIN [OnlyArts].[dbo].[Users] tb2 "
            + "ON tb1.[followed_user_id]  = tb2.[user_id] "
            + "WHERE tb1.[user_id] = ?";
    private static final String GET_FOLLOWER = "SELECT tb2.[user_id],[role_id],"
            + "[first_name],[last_name],[avatar],[phone],[email],[address],"
            + "[join_date],[bio],[status],[password] "
            + "FROM [OnlyArts].[dbo].[Followings] tb1 "
            + "RIGHT JOIN [OnlyArts].[dbo].[Users] tb2 "
            + "ON tb1.[user_id]  = tb2.[user_id] "
            + "WHERE tb1.[followed_user_id] = ?";

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

    public boolean addFollow(String userId, String userFollowedId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(ADD_FOLLOW);
            stm.setString(1, userId);
            stm.setString(2, userFollowedId);
            check = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on addfollow() method", ex);
            check = false;
        } finally {
            context.closeStatement(stm);
        }
        return check;
    }

    public boolean unfollowUser(String userId, String userFollowedId) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(UNFOLLOW);
            stm.setString(1, userId);
            stm.setString(2, userFollowedId);
            check = stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            logError("Exception found on addfollow() method", ex);
            check = false;
        } finally {
            context.closeStatement(stm);
        }
        return check;
    }

    public List<UserDTO> getFollowing(String userId) {
        List<UserDTO> followingList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = context.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(GET_FOLLOWING);
                stm.setString(1, userId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String following_userId = rs.getString("user_id");
                    String roleId = rs.getString("role_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String avatar = rs.getString("avatar");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    Date joinDate = rs.getDate("join_date");
                    String bio = rs.getString("bio");
                    int status = rs.getInt("status");
                    String password = "*****";
                    followingList.add(new UserDTO(following_userId, roleId, firstName, lastName, avatar, phone, email, address, joinDate, bio, status, password));
                }
            }
        } catch (SQLException e) {
            logError("Exception found on getFollowing()", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }

        return followingList;
    }

    public List<UserDTO> getFollower(String userId) {
        List<UserDTO> followerList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = context.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(GET_FOLLOWER);
                stm.setString(1, userId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    String following_userId = rs.getString("user_id");
                    String roleId = rs.getString("role_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String avatar = rs.getString("avatar");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    Date joinDate = rs.getDate("join_date");
                    String bio = rs.getString("bio");
                    int status = rs.getInt("status");
                    String password = "*****";
                    followerList.add(new UserDTO(following_userId, roleId, firstName, lastName, avatar, phone, email, address, joinDate, bio, status, password));
                }
            }
        } catch (SQLException e) {
            logError("Exception found on getFollower()", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return followerList;
    }
}
