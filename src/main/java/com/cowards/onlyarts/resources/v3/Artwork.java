package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.reaction.ReactionDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * This class represents endpoints for managing artworks, including updating,
 * deleting, and retrieving top 10 artworks.
 */
@Path("/v3/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();
    private static final OrderDetailDAO orderDetailDao = OrderDetailDAO.getInstance();

    /**
     * Endpoint for updating an artwork.
     *
     * @param artworkDTO The updated artwork data.
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the update operation.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);

            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout")).build();
            }

            if (!tokenDTO.getUserId().equals(artworkDTO.getOwnerId())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new TokenERROR("You are not allow update this artwork"))
                        .build();
            }

            boolean check = artworkDao.update(artworkDTO);

            if (check) {
                artworkDTO = artworkDao.getArtwork(artworkDTO.getArtworkId());
                return Response.ok(artworkDTO).build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ArtworkERROR("Cannot update artwork")).build();
        } catch (ArtworkERROR | TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Endpoint for deleting an artwork.
     *
     * @param artworkDTO The artwork data to be deleted.
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the delete operation.
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            UserDTO user = userDao.getUserById(tokenDTO.getUserId());

            if (!user.getUserId().equals(artworkDTO.getOwnerId())
                    || !"AD".equals(user.getRoleId())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new TokenERROR("You are not allow remove this artwork"))
                        .build();
            }

            String artworkId = artworkDTO.getArtworkId();

            boolean check = artworkDao.delete(artworkId);

            if (check) {
                artworkDTO = artworkDao.getArtwork(artworkId);
                return Response.ok(artworkDTO).build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ArtworkERROR("Cannot remove artwork"))
                    .build();
        } catch (ArtworkERROR | TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        } catch (UserERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex)
                    .build();
        }
    }

    /**
     * Endpoint for retrieving the top 10 artworks.
     *
     * @return Response containing a list of top 10 artworks.
     */
    @GET
    @Path("/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Artworks() {
        try {
            List<ArtworkDTO> list = artworkDao.getTop10();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @PUT
    @Path("/ban/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ban(@HeaderParam("authtoken") String tokenString,
            @PathParam("artworkId") String artworkId) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            UserDTO loginUser = userDao.getUserById(token.getUserId());
            if (!"AD".equals(loginUser.getRoleId())) {
                throw new TokenERROR("You dont have permission to do this action");
            } else {
                ArtworkDTO artwork = artworkDao.getArtwork(artworkId);
                if (artwork.isBanned()) {
                    throw new ArtworkERROR("This artwork already has been banned");
                } else {
                    boolean check = artworkDao.changeStatus(artworkId,
                            artwork.getStatus(), 0b010);
                    if (check) {
                        artwork = artworkDao.getArtwork(artworkId);
                        return Response.ok(artwork).build();
                    } else {
                        throw new ArtworkERROR("Cannot remove this artwork");
                    }
                }
            }
        } catch (TokenERROR ex) {
            return ex.getMessage().contains("permission")
                    ? Response.status(Response.Status.FORBIDDEN).entity(ex).build()
                    : Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        } catch (UserERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        } catch (ArtworkERROR ex) {
            return ex.getMessage().contains("exist")
                    ? Response.status(Response.Status.NOT_FOUND).entity(ex).build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE).entity(ex).build();
        }
    }

    @GET
    @Path("/isbuy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response isBuy(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            String userId = tokenDTO.getUserId();
            String artworkId = artworkDTO.getArtworkId();
            boolean check = orderDetailDao.isBuy(userId, artworkId);
            ReactionDTO reactionDTO = new ReactionDTO(artworkId, userId, check);
            if (check) {
                return Response.ok(reactionDTO).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("This artwork not buy yet").build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }
}
