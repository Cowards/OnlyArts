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
 * Resource class for handling user-related operations.
 */
@Path("v4/user")
public class User {

    private final UserDAO userDao = UserDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();

    /**
     * Retrieves the profile of the authenticated user.
     *
     * @param tokenString The authentication token of the user.
     * @return Response containing the user's profile details.
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
     * Retrieves a user's profile by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return Response containing the user's profile details.
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
     * Updates the user's information.
     *
     * @param user The updated user information.
     * @param tokenString The authentication token of the user.
     * @return Response containing the updated user's information.
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
     * Bans a user.
     *
     * @param tokenString The authentication token of the user performing the
     * ban action.
     * @param userId The ID of the user to be banned.
     * @return Response indicating the success or failure of the ban operation.
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
     * Unbans a user.
     *
     * @param tokenString The authentication token of the user performing the
     * unban action.
     * @param userId The ID of the user to be unbanned.
     * @return Response indicating the success or failure of the unban
     * operation.
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
