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
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Admin
 */
@Path("/v3/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

    @GET
    @Path("/update/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOne(@PathParam("artworkId") String artworkId,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);

            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            ArtworkDTO artworkDTO = artworkDao.getOne(artworkId);
            return Response.ok(artworkDTO).build();
        } catch (ArtworkERROR | TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @PUT
    @Path("/update")
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
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity(new TokenERROR("You are not allow update this artwork"))
                        .build();
            }

            return artworkDao.update(artworkDTO)
                    ? Response.ok(artworkDTO).build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE)
                            .entity(new ArtworkERROR("Cannot update this artwork"))
                            .build();
        } catch (ArtworkERROR | TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @DELETE
    @Path("/delete/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("artworkId") String artworkId,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            ArtworkDTO artworkDTO = artworkDao.getOne(artworkId);

            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            if (!tokenDTO.getUserId().equals(artworkDTO.getOwnerId())) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity(new TokenERROR("You are not allow remove this artwork"))
                        .build();
            }

            return artworkDao.delete(artworkId)
                    ? Response.ok(artworkDTO).build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE)
                            .entity(new ArtworkERROR("Cannot remove this artwork"))
                            .build();
        } catch (ArtworkERROR | TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }
}
