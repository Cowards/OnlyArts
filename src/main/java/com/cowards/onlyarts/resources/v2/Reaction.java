package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.reaction.ReactionDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.services.ReactionDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * This class represents the endpoints for managing reactions, including viewing
 * users who reacted to a specific artwork and adding reactions to artworks.
 */
@Path("v2/reactions")
public class Reaction {

    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final ReactionDAO reactionDao = ReactionDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    @PUT
    @Path("/{artwork_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkReact(@PathParam("artwork_id") String artworkId,
            @HeaderParam("authtoken") String tokenString) throws ArtworkERROR {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = reactionDao.checkReaction(token.getUserId(), artworkId);
            ReactionDTO react = new ReactionDTO(artworkId, token.getUserId(), check);
            return Response.ok(react).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex).build();
        }
    }

    @DELETE
    @Path("/{artwork_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeReact(@PathParam("artwork_id") String artworkId,
            @HeaderParam("authtoken") String tokenString) throws ArtworkERROR {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = reactionDao.removeReaction(token.getUserId(), artworkId);
            ReactionDTO react = new ReactionDTO(artworkId, token.getUserId(), check);
            return Response.ok(react).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex).build();
        }
    }

    /**
     * Endpoint for viewing users who reacted to a specific artwork.
     *
     * @param artworkId The ID of the artwork to view reactions for.
     * @return Response containing a list of users who reacted to the specified
     * artwork.
     * @throws ArtworkERROR If there's an error retrieving the artwork.
     */
    @GET
    @Path("/{artwork_id}")
    public Response viewReactUser(@PathParam("artwork_id") String artworkId) throws ArtworkERROR {
        List<UserDTO> userList = userDao.getUserReaction(artworkId);
        return Response
                .ok(userList, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Endpoint for adding a reaction to an artwork.
     *
     * @param tokenString The authentication token.
     * @param reaction The reaction data to add.
     * @return Response indicating success or failure of adding the reaction.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReaction(@HeaderParam("authtoken") String tokenString,
            ReactionDTO reaction) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            String artworkId = reaction.getArtworkId();
            boolean checkAddNewReaction = reactionDao.addReaction(userId, artworkId);
            return checkAddNewReaction
                    ? Response.status(Response.Status.OK)
                            .entity(new ReactionDTO(artworkId, userId, checkAddNewReaction))
                            .build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }
}
