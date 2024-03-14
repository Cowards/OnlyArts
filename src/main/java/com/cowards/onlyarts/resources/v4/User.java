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

@Path("v4/user")
public class User {

    private final UserDAO userDao = UserDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();

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
                    return Response.status(200).build();
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

    @PUT
    @Path("ban/{userid}")
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
                        return Response.status(200).build();
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

    @PUT
    @Path("unban/{userid}")
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
                        return Response.status(200).build();
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
