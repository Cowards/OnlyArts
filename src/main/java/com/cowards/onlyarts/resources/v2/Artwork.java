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
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the endpoints for managing artworks, including
 * retrieval, searching, publishing, and selling.
 */
@Path("v2/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    /**
     * Endpoint for retrieving all artworks.
     *
     * @return Response containing a list of all artworks.
     */
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
    @Path("/offset/{offset}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOffset(@PathParam("offset") int offset) {
        List<ArtworkDTO> artworks = artworkDao.getAll();
        int i = (offset - 1) * 28 + 1;
        int j = offset * 28;
        List< ArtworkDTO> _artworks = new ArrayList<>();
        for (int k = i; k <= j && k < artworks.size(); k++) {
            _artworks.add(artworks.get(k));
        }
        return Response.ok(_artworks).build();
    }

    /**
     * Endpoint for retrieving a specific artwork by its ID.
     *
     * @param artworkId The ID of the artwork to retrieve.
     * @return Response containing the artwork information.
     */
    @GET
    @Path("/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)

    public Response get(@PathParam("artworkId") String artworkId) {
        try {
            ArtworkDTO artwork = artworkDao.getArtwork(artworkId);
            if (artwork.isRemoved() || artwork.isBanned()) {
                throw new ArtworkERROR("Artwork is removed");
            }
            return Response.status(Response.Status.OK)
                    .entity(artwork)
                    .build();
        } catch (ArtworkERROR ex) {
            return Response.status(422)
                    .entity(ex)
                    .build();
        }
    }

    /**
     * Endpoint for searching artworks by type.
     *
     * @param typeInput The type of artwork to search for.
     * @return Response containing a list of artworks matching the type.
     */
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

    /**
     * Endpoint for searching artworks by title.
     *
     * @param titleInput The title of the artwork to search for.
     * @return Response containing a list of artworks matching the title.
     */
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

    /**
     * Endpoint for searching artworks by creator.
     *
     * @param creatorInput The name of the creator to search for.
     * @return Response containing a list of artworks created by the specified
     * creator.
     */
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

    /**
     * Endpoint for adding a new artwork.
     *
     * @param tokenString The authentication token.
     * @param artwork The artwork information to add.
     * @return Response indicating success or failure of the artwork addition.
     */
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

    /**
     * Endpoint for selling an artwork.
     *
     * @param tokenString The authentication token.
     * @param artwork The artwork information to sell.
     * @return Response indicating success or failure of the artwork sale.
     */
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
