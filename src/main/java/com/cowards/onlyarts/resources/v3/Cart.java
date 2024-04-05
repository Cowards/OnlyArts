package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.cart.CartERROR;
import com.cowards.onlyarts.repositories.reaction.ReactionDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CartDAO;
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
 * This class represents endpoints for managing the user's cart, including
 * adding, removing, and retrieving cart items.
 */
@Path("/v3/cart")
public class Cart {

    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    @GET
    @Path("{artworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAdded(@HeaderParam("authtoken") String tokenString,
            @PathParam("artworkId") String artworkId) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            String userId = tokenDTO.getUserId();
            UserDTO user = userDao.getUserById(userId);
            if ("CR".equals(user.getRoleId())) {
                throw new UserERROR("You dont have permission to add this artwork to cart");
            }
            boolean check = cartDao.checkAdded(userId, artworkId);
            return Response.ok(new ReactionDTO(artworkId, userId, check)).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e).build();
        } catch (UserERROR ex) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving the user's cart items.
     *
     * @param tokenString The authentication token.
     * @return Response containing a list of cart items.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            String userId = tokenDTO.getUserId();
            List<ArtworkDTO> artworkDTOs = cartDao.getAll(userId);
            return Response.ok(artworkDTOs).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Endpoint for adding an artwork to the user's cart.
     *
     * @param artworkDTO The artwork to be added to the cart.
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the add operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout")).build();
            }

            String userId = tokenDTO.getUserId();
            String artworkId = artworkDTO.getArtworkId();
            artworkDTO = artworkDao.getArtwork(artworkId);
            UserDTO user = userDao.getUserById(userId);
            if ("CR".equals(user.getRoleId())) {
                throw new UserERROR("You dont have permission to add this artwork to cart");
            }
            if (artworkDTO.isPrivate()
                    || artworkDTO.isBanned()
                    || artworkDTO.isRemoved()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new CartERROR("This artwork cannot be added to cart"))
                        .build();
            }

            boolean check = cartDao.insert(userId, artworkId);
            if (check) {
                return Response.ok(artworkDTO).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CartERROR("You have already added this artwork to your cart")).build();
        } catch (TokenERROR | UserERROR | ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Endpoint for removing an artwork from the user's cart.
     *
     * @param artworkDTO The artwork to be removed from the cart.
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the remove operation.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(ArtworkDTO artworkDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            String userId = tokenDTO.getUserId();
            String artworkId = artworkDTO.getArtworkId();
            boolean check = cartDao.delete(userId, artworkId);
            if (check) {
                artworkDTO = artworkDao.getArtwork(artworkId);
                return Response.ok(artworkDTO).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot remove this artwork from cart").build();
        } catch (TokenERROR | ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Endpoint for removing all artworks from the user's cart.
     *
     * @param tokenString The authentication token.
     * @return Response indicating success or failure of the remove operation.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            String userId = tokenDTO.getUserId();
            List<ArtworkDTO> artworkDTOs = cartDao.getAll(userId);
            boolean check = cartDao.delete(userId);
            if (check) {
                return Response.ok(artworkDTOs).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot remove this cart").build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }
}
