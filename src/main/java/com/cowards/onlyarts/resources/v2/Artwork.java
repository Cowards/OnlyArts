package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.comment.CommentDTO;
import com.cowards.onlyarts.repositories.reaction.ReactionDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CommentDAO;
import com.cowards.onlyarts.services.FavorDAO;
import com.cowards.onlyarts.services.ReactionDAO;
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
    private static final CommentDAO commentDao = CommentDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();
    private static final ReactionDAO reactionDao = ReactionDAO.getInstance();
    private static final FavorDAO favorDao = FavorDAO.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<ArtworkDTO> artworks = artworkDao.getAll();
        if (!artworks.isEmpty()) {
            return Response.ok(artworks).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("artworkId") String artworkId) {
        try {
            ArtworkDTO artwork = artworkDao.getArtwork(artworkId);
            return Response.status(200)
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
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<ArtworkDTO> listArtwork = artworkDao.getListArtworkWithType(typeInput);
        if (!listArtwork.isEmpty()) {
            return Response.ok(listArtwork, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/title/{title_input}")
    public Response searchByName(@PathParam("title_input") String titleInput) {
        if (titleInput.isBlank()) {
            return Response.status(Response.Status.NOT_FOUND).build();
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
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<ArtworkDTO> list = artworkDao.getListArtworkWithNameOfCreator(creatorInput);
        if (!list.isEmpty()) {
            return Response.ok(list, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/comment/{artwork_id}")
    public Response viewComment(@PathParam("artwork_id") String artworkId) throws ArtworkERROR {
        List<CommentDTO> comment = commentDao.getArtworkComment(artworkId);
        if (!comment.isEmpty()) {
            return Response.ok(comment, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/reaction/{artwork_id}")
    public Response viewReactUser(@PathParam("artwork_id") String artworkId) throws ArtworkERROR {
        List<UserDTO> userList = userDao.getUserReaction(artworkId);
        if (!userList.isEmpty()) {
            return Response
                    .ok(userList, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response comment(@HeaderParam("authtoken") String tokenString,
            CommentDTO comment) throws TokenERROR {
        TokenDTO token = tokenDao.getToken(tokenString);
        String userId = token.getUserId();
        String commentId = CodeGenerator.generateUUID(20);
        comment.setCommentId(commentId);
        comment.setCommenterId(userId);
        boolean checkAddNewComment = commentDao.addComment(comment);
        if (checkAddNewComment) {
            comment = commentDao.getComment(commentId);
            return Response.ok(comment).build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("/reaction")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReaction(@HeaderParam("authtoken") String tokenString,
            ReactionDTO reaction) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            String artworkId = reaction.getArtworkId();
            boolean checkAddNewReaction = reactionDao.addReaction(userId, artworkId);
            return checkAddNewReaction
                    ? Response.status(Response.Status.NO_CONTENT).build()
                    : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }

    @GET
    @Path("/favorite/{user_id}")
    public Response viewFavorite(@PathParam("user_id") String userId) {
        List<ArtworkDTO> favoArtworks = favorDao.getFavoriteArtworks(userId);
        if (!favoArtworks.isEmpty()) {
            return Response.ok(favoArtworks, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/favorite")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFavorite(@HeaderParam("authtoken") String tokenString,
            ArtworkDTO artworkDTO) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            String artworkId = artworkDTO.getArtworkId();
            boolean checkAddNewFavorite = favorDao.addFavorite(userId, artworkId);
            return checkAddNewFavorite
                    ? Response.status(Response.Status.NO_CONTENT).build()
                    : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }

    @POST
    @Path("/publish")
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
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(401)
                    .entity(ex).build();
        }
    }

    @PUT
    @Path("/sell")
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
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (TokenERROR | ArtworkERROR ex) {
            return Response.status(401)
                    .entity(ex).build();
        }
    }

}
