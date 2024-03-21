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

@Path("v4/favor")
public class Favor {

    private static final FavorDAO favorDao = FavorDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

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
