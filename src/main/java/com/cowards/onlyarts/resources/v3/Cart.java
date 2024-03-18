/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.cart.CartDTO;
import com.cowards.onlyarts.repositories.cart.CartERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.CartDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Admin
 */
@Path("/v3/cart")
public class Cart {

    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam("authtoken") String tokeString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokeString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            List<CartDTO> cartDTOs = cartDao.getAll(tokenDTO.getUserId());
            return Response.ok(cartDTOs).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

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

            if (artworkDTO.isPrivate()
                    || artworkDTO.isBanned()
                    || artworkDTO.isRemoved()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new CartERROR("This artwork cannot be added to cart"))
                        .build();
            }

            String userId = tokenDTO.getUserId();
            String artworkId = artworkDTO.getArtworkId();

            CartDTO cartDTO = new CartDTO(userId, artworkId);
            return cartDao.insert(cartDTO)
                    ? Response.ok(cartDTO).build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE)
                            .build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @DELETE
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

            CartDTO cartDTO = new CartDTO(userId, artworkId);
            return cartDao.delete(cartDTO)
                    ? Response.ok(cartDTO).build()
                    : Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
