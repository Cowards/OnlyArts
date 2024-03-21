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

/*
 * This class defines RESTful endpoints related to user follow actions.
 */
@Path("v4/follow")
public class Follow {

    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final FollowDAO followDao = FollowDAO.getInstance();

    /**
     * Endpoint for a user to follow another user.
     *
     * @param tokenString The authentication token of the user.
     * @param userId The ID of the user to follow.
     * @return Response indicating success or failure of the follow action.
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
     * Endpoint for a user to unfollow another user.
     *
     * @param tokenString The authentication token of the user.
     * @param userId The ID of the user to unfollow.
     * @return Response indicating success or failure of the unfollow action.
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
     * Retrieves the list of users that the specified user is following.
     *
     * @param userId The ID of the user whose following list is to be retrieved.
     * @return Response containing the list of users being followed if available,
     *         or a NO_CONTENT response if the list is empty.
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
     * Retrieves the list of users who are following the specified user.
     *
     * @param userId The ID of the user whose followers are to be retrieved.
     * @return Response containing the list of followers if available,
     *         or a NO_CONTENT response if the list is empty.
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
