package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Admin
 */
@Path("/v3/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

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

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);

            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            if (!tokenDTO.getUserId().equals(artworkDTO.getOwnerId())) {
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
        }
    }

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
}
