package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.reaction.ReactionDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.FavorDAO;
import com.cowards.onlyarts.services.TokenDAO;
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
 * This class represents the endpoints for managing favorites, including viewing
 * favorite artworks and adding artworks to favorites.
 */
@Path("v2/favorite")
public class Favor {

    private static final FavorDAO favorDao = FavorDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

    @PUT
    @Path("/{artwork_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkFavor(@PathParam("artwork_id") String artworkId,
            @HeaderParam("authtoken") String tokenString) throws ArtworkERROR {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = favorDao.checkFavorite(token.getUserId(), artworkId);
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
    public Response remoceFavor(@PathParam("artwork_id") String artworkId,
            @HeaderParam("authtoken") String tokenString) throws ArtworkERROR {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            boolean check = favorDao.removeFavorite(token.getUserId(), artworkId);
            ReactionDTO react = new ReactionDTO(artworkId, token.getUserId(), check);
            return Response.ok(react).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex).build();
        }
    }

    /**
     * Endpoint for viewing favorite artworks of a specific user.
     *
     * @param userId The ID of the user whose favorite artworks to view.
     * @return Response containing a list of favorite artworks for the specified
     * user.
     */
    @GET
    @Path("/{user_id}")
    public Response viewFavorite(@PathParam("user_id") String userId) {
        List<ArtworkDTO> favoArtworks = favorDao.getFavoriteArtworks(userId);
        return Response.ok(favoArtworks, MediaType.APPLICATION_JSON).build();

    }

    /**
     * Endpoint for adding an artwork to favorites.
     *
     * @param tokenString The authentication token.
     * @param artworkDTO The artwork data to add to favorites.
     * @return Response indicating success or failure of adding artwork to
     * favorites.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFavorite(@HeaderParam("authtoken") String tokenString,
            ArtworkDTO artworkDTO) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            String artworkId = artworkDTO.getArtworkId();
            boolean checkAddNewFavorite = favorDao.addFavorite(userId, artworkId);
            return checkAddNewFavorite
                    ? Response.status(Response.Status.OK)
                            .entity(new ReactionDTO(artworkId, userId, checkAddNewFavorite))
                            .build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

}
