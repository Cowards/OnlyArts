package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.core.Password;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access operations for managing user data.
 */
public class UserDAO {

    private static final DBContext context = DBContext.getInstance();
    private static UserDAO instance;
    private static final String GET_USERS
            = "SELECT [user_id]"
            + ",[role_id],[first_name],[last_name],[phone],[email]"
            + ",[address],[avatar],[join_date],[bio],[status],[password]"
            + " FROM [dbo].[Users]";
    private static final String CHANGE_PASSWORD
            = "UPDATE [dbo].[Users] "
            + "SET [password] = ? "
            + "WHERE [user_id] = ?";
    private static final String CHANGE_STATUS
            = "UPDATE [Users] "
            + "SET [status] = ? "
            + "WHERE [user_id] = ?";
    private static final String ADD_USER
            = "INSERT INTO [dbo].[Users]"
            + "([user_id],[role_id],[first_name],[last_name],[phone],"
            + "[email],[address],[bio],[status],[password],[avatar])"
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_USER_INFO
            = "UPDATE [dbo].[Users] SET "
            + "[first_name] = ?,"
            + "[last_name] =? ,"
            + "[phone] = ?,"
            + "[address] = ?,"
            + "[bio] = ? ,"
            + "[avatar] = ? "
            + "WHERE [user_id] = ? ";
    private static final String GET_USER_REACTION
            = "SELECT tb2.[user_id]"
            + ",[role_id],[first_name],[last_name],[phone],[email]"
            + ",[address],[avatar],[join_date],[bio],[status],[password]"
            + " FROM [dbo].[Users] tb1 "
            + "RIGHT JOIN Artwork_reactions tb2 "
            + "ON tb1.user_id = tb2.user_id"
            + " WHERE [artwork_id] = ?";
    private static final String GET_TOP_10_USERS
            = "SELECT TOP (10) [user_id]"
            + ",[role_id],[first_name],[last_name],[phone],[email]"
            + ",[address],[avatar],[join_date],[bio],[status],[password]"
            + " FROM [dbo].[Users]"
            + " ORDER BY [join_date] DESC";
    private static final String GET_FOLLOWER = "SELECT tb2.[user_id],[role_id],"
            + "[first_name],[last_name],[avatar],[phone],[email],[address],"
            + "[join_date],[bio],[status],[password] "
            + "FROM [OnlyArts].[dbo].[Followings] tb1 "
            + "RIGHT JOIN [OnlyArts].[dbo].[Users] tb2 "
            + "ON tb1.[user_id]  = tb2.[user_id] "
            + "WHERE tb1.[followed_user_id] = ?";

    private void logError(String message, Exception ex) {
        Logger.getLogger(UserDAO.class.getName())
                .log(Level.SEVERE, message, ex);
    }

    /**
     * Private constructor for the UserDAO class. Prevents instantiation from
     * outside the class.
     */
    private UserDAO() {
    }

    /**
     * Gets the instance of UserDAO using the singleton pattern. If the instance
     * is null, a new instance is created.
     *
     * @return The instance of UserDAO.
     */
    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    /**
     * Adds a new user to the system.
     *
     * @param user The user object to add.
     * @return The user object with generated user ID.
     */
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
            stm.setString(8, user.getBio());
            stm.setInt(9, 0b000);
            stm.setString(10, Password.hashPw(user.getPassword()));
            stm.setString(11, user.getAvatar());
            stm.executeUpdate();
            user.setUserId(userId);
        } catch (SQLException ex) {
            logError("Exception found on addNewUser() method", ex);
        } finally {
            context.closeStatement(stm);
        }
        return user;
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve.
     * @return The UserDTO object representing the user.
     * @throws UserERROR if the user with the specified email does not exist.
     */
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
                user.setStatus(rs.getInt("status"));
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
        }
        return user;
    }

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId The user ID of the user to retrieve.
     * @return The UserDTO object representing the user.
     * @throws UserERROR if the user with the specified user ID does not exist.
     */
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
        }
        return user;
    }

    /**
     * Changes the status of a user.
     *
     * @param userId The user ID of the user whose status is to be changed.
     * @param status The new status value.
     * @param state The state of the status to be changed (e.g., activate or
     * deactivate).
     * @return True if the status is changed successfully, otherwise false.
     */
    public boolean changeStatus(String userId, int status, int state) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(CHANGE_STATUS);
            stm.setInt(1, status ^ state);
            stm.setString(2, userId);
            stm.executeUpdate();
            res = true;
        } catch (SQLException ex) {
            logError("Exception found on changeStatus() method", ex);
            res = false;
        } finally {
            context.closeStatement(stm);
        }
        return res;
    }

    /**
     * Changes the password of a user.
     *
     * @param userId The user ID of the user whose password is to be changed.
     * @param newPw The new password.
     * @return True if the password is changed successfully, otherwise false.
     */
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

    /**
     * Updates the information of a user.
     *
     * @param user The UserDTO object containing the updated information.
     * @return True if the user information is updated successfully, otherwise
     * false.
     */
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
                stm.setString(4, user.getAddress());
                stm.setString(5, user.getBio());
                stm.setString(6, user.getAvatar());
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

    /**
     * Retrieves the list of users who reacted to a specific artwork.
     *
     * @param artworkId The ID of the artwork.
     * @return The list of UserDTO objects representing users who reacted to the
     * artwork.
     */
    public List<UserDTO> getUserReaction(String artworkId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<UserDTO> list = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USER_REACTION);
            stm.setString(1, artworkId);
            rs = stm.executeQuery();
            while (rs.next()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(rs.getString("user_id"));
                userDTO.setFirstName(rs.getString("first_name"));
                userDTO.setLastName(rs.getString("last_name"));
                userDTO.setRoleId(rs.getString("role_id"));
                userDTO.setPhone(rs.getString("phone"));
                userDTO.setEmail(rs.getString("email"));
                userDTO.setAddress(rs.getString("address"));
                userDTO.setJoinDate(rs.getDate("join_date"));
                userDTO.setBio(rs.getString("bio"));
                userDTO.setStatus(rs.getInt("status"));
                userDTO.setAvatar(rs.getString("avatar"));
                userDTO.setPassword(rs.getString("password"));
                list.add(userDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getUserReaction(String artworkId) method", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }

    /**
     * Retrieves the list of all users in the system.
     *
     * @return The list of UserDTO objects representing all users in the system.
     */
    public List<UserDTO> getAllUsers() {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<UserDTO> userList = new ArrayList<>();

        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USERS);
            rs = stm.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO();
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
                userList.add(user);
            }
        } catch (SQLException ex) {
            logError("Exception found on getAllUser() method", ex);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }

        return userList;
    }

    /**
     * Retrieves the list of followers for a specific user.
     *
     * @param userId The ID of the user.
     * @return The list of UserDTO objects representing the followers of the
     * user.
     */
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

    /**
     * Retrieves the top 10 users based on their join date.
     *
     * @return The list of UserDTO objects representing the top 10 users.
     */
    public List<UserDTO> getTop10() {
        List<UserDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_TOP_10_USERS);
            rs = stm.executeQuery();
            while (rs.next()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(rs.getString("user_id"));
                userDTO.setFirstName(rs.getString("first_name"));
                userDTO.setLastName(rs.getString("last_name"));
                userDTO.setRoleId(rs.getString("role_id"));
                userDTO.setPhone(rs.getString("phone"));
                userDTO.setEmail(rs.getString("email"));
                userDTO.setAddress(rs.getString("address"));
                userDTO.setJoinDate(rs.getDate("join_date"));
                userDTO.setBio(rs.getString("bio"));
                userDTO.setStatus(rs.getInt("status"));
                userDTO.setAvatar(rs.getString("avatar"));
                userDTO.setPassword(rs.getString("password"));
                list.add(userDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getTop10() method", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }

    public List<UserDTO> getAllUserByRole(String roleId) {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<UserDTO> list = new ArrayList<>();
        try {
            conn = context.getConnection();
            stm = conn.prepareStatement(GET_USERS + "WHERE role_id = ?");
            stm.setString(1, roleId);
            rs = stm.executeQuery();
            while (rs.next()) {
                UserDTO userDTO = new UserDTO(rs.getString(1),
                        rs.getString(roleId), rs.getString(3),
                        rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7),
                        rs.getString(8), rs.getDate(9),
                        rs.getString(10), rs.getInt(11),
                        rs.getString(12));
                list.add(userDTO);
            }
        } catch (SQLException e) {
            logError("Exception found on getAllUserByRole() method", e);
        } finally {
            context.closeResultSet(rs);
            context.closeStatement(stm);
        }
        return list;
    }
}
