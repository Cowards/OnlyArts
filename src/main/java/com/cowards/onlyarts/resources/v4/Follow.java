package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.services.FollowDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource class for managing user follows in the OnlyArts system. This class
 * provides endpoints for following and unfollowing users, as well as retrieving
 * lists of users being followed and users who follow a specified user.
 *
 * This class interacts with the FollowDAO and TokenDAO to handle operations
 * related to user follows.
 */
@Path("v4/follow")
public class Follow {

    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final FollowDAO followDao = FollowDAO.getInstance();

    /**
     * Endpoint for following a user. This method adds a follow relationship
     * between the authenticated user and the specified user.
     *
     * @param tokenString The authentication token.
     * @param userId The ID of the user to follow.
     * @return Response indicating success or failure of the operation.
     */
    @POST
    @Path("{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response followUser(@HeaderParam("authtoken") String tokenString,
            @PathParam("userid") String userId) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = followDao.addFollow(token.getUserId(), userId);
            if (check) {
                return Response.status(200).build();
            } else {
                throw new TokenERROR("Cannot follow this user");
            }
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        }
    }

    /**
     * Endpoint for unfollowing a user. This method removes the follow
     * relationship between the authenticated user and the specified user.
     *
     * @param tokenString The authentication token.
     * @param userId The ID of the user to unfollow.
     * @return Response indicating success or failure of the operation.
     */
    @DELETE
    @Path("{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unfollowUser(@HeaderParam("authtoken") String tokenString,
            @PathParam("userid") String userId) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = followDao.unfollowUser(token.getUserId(), userId);
            if (check) {
                return Response.status(200).build();
            } else {
                throw new TokenERROR("Cannot unfollow this user");
            }
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving the users followed by a specified user. This
     * method returns a list of users followed by the specified user.
     *
     * @param userId The ID of the user to retrieve followed users for.
     * @return Response containing the list of followed users.
     */
    @GET
    @Path("following/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFollowing(@PathParam("userid") String userId) {
        List<UserDTO> followingList = followDao.getFollowing(userId);
        return !followingList.isEmpty()
                ? Response.status(Response.Status.OK).entity(followingList).build()
                : Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Endpoint for retrieving the users who follow a specified user. This
     * method returns a list of users who follow the specified user.
     *
     * @param userId The ID of the user to retrieve followers for.
     * @return Response containing the list of followers.
     */
    @GET
    @Path("follower/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFollower(@PathParam("userid") String userId) {
        List<UserDTO> followerList = followDao.getFollower(userId);
        return !followerList.isEmpty()
                ? Response.status(Response.Status.OK).entity(followerList).build()
                : Response.status(Response.Status.NO_CONTENT).build();

    }

}
