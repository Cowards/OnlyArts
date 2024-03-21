package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The User class provides endpoints for managing user profiles and account
 * status in the OnlyArts system. This class allows users to retrieve user
 * profiles, update user information, ban/unban user accounts, etc.
 */
@Path("v4/user")
public class User {

    private final UserDAO userDao = UserDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();

    /**
     * Endpoint for retrieving the user profile. This method retrieves the
     * profile of the user associated with the provided authentication token.
     *
     * @param tokenString The authentication token.
     * @return Response containing the user profile.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProfile(@HeaderParam("authtoken") String tokenString) {
        try {
            UserDTO user = null;
            TokenDTO token = tokenDao.getToken(tokenString);
            user = userDao.getUserById(token.getUserId());
            return Response.status(Response.Status.OK)
                    .entity(user)
                    .build();
        } catch (UserERROR | TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex)
                    .build();
        }
    }

    /**
     * Endpoint for retrieving a user by ID. This method retrieves a user
     * profile based on the provided user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return Response containing the retrieved user profile.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{user_id}")
    public Response getUser(@PathParam("user_id") String userId) {
        try {
            UserDTO user = userDao.getUserById(userId);
            return Response.status(Response.Status.OK)
                    .entity(user)
                    .build();
        } catch (UserERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex)
                    .build();
        }
    }

    /**
     * Endpoint for updating user information. This method updates the
     * information of the user associated with the provided authentication
     * token.
     *
     * @param user The updated user information.
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(UserDTO user, @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            UserDTO loginUser = userDao.getUserById(token.getUserId());
            if (token.getUserId().equals(user.getUserId())
                    || "AD".equals(loginUser.getRoleId())) {
                boolean checkUpdate = userDao.updateUserInfo(user);
                if (checkUpdate) {
                    loginUser = userDao.getUserById(token.getUserId());
                    return Response.status(200).entity(loginUser).build();
                } else {
                    throw new TokenERROR("You cannot update this account information");
                }
            } else {
                throw new TokenERROR("You cannot update this account information");
            }
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        } catch (UserERROR ex) {
            return Response.status(404).entity(ex).build();
        }
    }

    /**
     * Endpoint for banning a user account. This method bans the user account
     * associated with the provided user ID.
     *
     * @param tokenString The authentication token.
     * @param userId The ID of the user account to ban.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("ban/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response banUser(@HeaderParam("authtoken") String tokenString,
            @PathParam("userid") String userId) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            UserDTO loginUser = userDao.getUserById(token.getUserId());
            UserDTO user = userDao.getUserById(userId);
            if (!"AD".equals(loginUser.getRoleId())) {
                throw new TokenERROR("You do not have permission to ban this account");
            } else {
                if (user.isBanned()) {
                    throw new UserERROR("This user already has been banned");
                } else {
                    boolean check = userDao.changeStatus(user.getUserId(),
                            user.getStatus(), 0b100);
                    if (check) {
                        user = userDao.getUserById(userId);
                        return Response.status(200).entity(user).build();
                    } else {
                        throw new UserERROR("Cannot ban this user");
                    }
                }
            }
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        } catch (UserERROR ex) {
            return ex.getMessage().contains("exist")
                    ? Response.status(404).entity(ex).build()
                    : Response.status(406).entity(ex).build();
        }
    }

    /**
     * Endpoint for unbanning a user account. This method unbans the user
     * account associated with the provided user ID.
     *
     * @param tokenString The authentication token.
     * @param userId The ID of the user account to unban.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("unban/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unbanUser(@HeaderParam("authtoken") String tokenString,
            @PathParam("userid") String userId) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            UserDTO loginUser = userDao.getUserById(token.getUserId());
            UserDTO user = userDao.getUserById(userId);
            if (!"AD".equals(loginUser.getRoleId())) {
                throw new TokenERROR("You do not have permission to unban this account");
            } else {
                if (!user.isBanned()) {
                    throw new UserERROR("This user is not banned");
                } else {
                    boolean check = userDao.changeStatus(user.getUserId(),
                            user.getStatus(), 0b100);
                    if (check) {
                        user = userDao.getUserById(userId);
                        return Response.status(200).entity(user).build();
                    } else {
                        throw new UserERROR("Cannot unban this user");
                    }
                }
            }
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        } catch (UserERROR ex) {
            return ex.getMessage().contains("exist")
                    ? Response.status(404).entity(ex).build()
                    : Response.status(406).entity(ex).build();
        }
    }

}
