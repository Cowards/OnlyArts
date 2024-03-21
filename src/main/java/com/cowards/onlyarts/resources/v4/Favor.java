package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.FavorDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource class for managing user favorites in the OnlyArts system. This class
 * provides endpoints for retrieving favorite artworks for a user, both for the
 * currently authenticated user and for a specified user ID.
 *
 * This class interacts with the FavorDAO, TokenDAO, and UserDAO to handle
 * operations related to user favorites.
 */
@Path("v4/favor")
public class Favor {

    private static final FavorDAO favorDao = FavorDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    /**
     * Retrieves favorite artworks for the currently authenticated user. This
     * method fetches the favorite artworks for the user associated with the
     * provided authentication token and returns them as a list.
     *
     * @param tokenString The authentication token associated with the user.
     * @return Response containing the list of favorite artworks.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoriteArtworks(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            List<ArtworkDTO> artworks = favorDao.getFavoriteArtworks(token.getUserId());
            return Response.status(200).entity(artworks).build();
        } catch (TokenERROR ex) {
            return Response.status(401).entity(ex).build();
        }
    }

    /**
     * Retrieves favorite artworks for a specified user. This method fetches the
     * favorite artworks for the user specified by the provided user ID and
     * returns them as a list.
     *
     * @param userId The ID of the user to fetch favorite artworks for.
     * @return Response containing the list of favorite artworks.
     */
    @GET
    @Path("{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserFavoriteArtwroks(@PathParam("userid") String userId) {
        try {
            UserDTO user = userDao.getUserById(userId);
            List<ArtworkDTO> artworks = favorDao.getFavoriteArtworks(user.getUserId());
            return Response.status(200).entity(artworks).build();
        } catch (UserERROR ex) {
            return Response.status(404).entity(ex).build();
        }
    }
}
