package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
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

@Path("v2/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<ArtworkDTO> artworks = artworkDao.getAll();
        if (!artworks.isEmpty()) {
            return Response.ok(artworks).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("artworkId") String artworkId) {
        try {
            ArtworkDTO artwork = artworkDao.getArtwork(artworkId);
            return Response.status(Response.Status.OK)
                    .entity(artwork)
                    .build();
        } catch (ArtworkERROR ex) {
            return Response.status(404)
                    .entity(ex)
                    .build();
        }
    }

    @GET
    @Path("/type/{type_input}")
    public Response searchByType(@PathParam("type_input") String typeInput) {
        if (typeInput.isBlank()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<ArtworkDTO> listArtwork = artworkDao.getListArtworkWithType(typeInput);
        if (!listArtwork.isEmpty()) {
            return Response.ok(listArtwork, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/title/{title_input}")
    public Response searchByName(@PathParam("title_input") String titleInput) {
        if (titleInput.isBlank()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<ArtworkDTO> list = artworkDao.getListArtworkWithName(titleInput);
        if (!list.isEmpty()) {
            return Response.ok(list, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/creator/{creator_input}")
    public Response searchByNameOfCreator(@PathParam("creator_input") String creatorInput) {
        if (creatorInput.isBlank()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<ArtworkDTO> list = artworkDao.getListArtworkWithNameOfCreator(creatorInput);
        if (!list.isEmpty()) {
            return Response.ok(list, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addArtwork(@HeaderParam("authtoken") String tokenString,
            ArtworkDTO artwork) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            UserDTO userDTO = userDao.getUserById(token.getUserId());
            if (!userDTO.getRoleId().equals("CR")) {
                throw new TokenERROR("You cannot publish this artwork");
            }
            String artworkId = CodeGenerator.generateUUID(20);
            artwork.setArtworkId(artworkId);
            artwork.setOwnerId(token.getUserId());
            boolean check = artworkDao.addArtwork(artwork);
            if (check) {
                return Response.ok(artwork).build();
            }
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(401)
                    .entity(ex).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellArtwork(@HeaderParam("authtoken") String tokenString,
            ArtworkDTO artwork) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            if (!token.getUserId().equals(artwork.getOwnerId())) {
                throw new TokenERROR("You don't own this artwork");
            }
            boolean check = artworkDao.updateArtworkPrice(artwork);
            if (check) {
                artwork = artworkDao.getArtwork(artwork.getArtworkId());
                return Response.ok(artwork).build();
            }
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (TokenERROR | ArtworkERROR ex) {
            return Response.status(401)
                    .entity(ex).build();
        }
    }

}
