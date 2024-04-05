package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.CartDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * This class represents endpoints for managing order details, including
 * retrieving all order details for a specific order and inserting order details
 * for a given order ID.
 */
@Path("/v3/orderdetails")
public class OrderDetail {

    private static final OrderDetailDAO orderDetailDao = OrderDetailDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final CartDAO cartDao = CartDAO.getInstance();

    /**
     * Endpoint for retrieving all order details for a specific order.
     *
     * @param orderId The ID of the order.
     * @return Response containing a list of order details.
     */
    @GET
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("orderId") String orderId) {
        try {
            List<ArtworkDTO> list = orderDetailDao.getAll(orderId);
            return Response.ok(list).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }

    /**
     * Endpoint for inserting order details for a given order ID.
     *
     * @param tokenString The authentication token.
     * @param orderId The ID of the order.
     * @return Response indicating success or failure of the insert operation.
     */
    @POST
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(@HeaderParam("authtoken") String tokenString,
            @PathParam("orderId") String orderId) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            String userId = tokenDTO.getUserId();
            List<ArtworkDTO> artworkDTOs = cartDao.getAll(userId);
            boolean check = false;
            for (ArtworkDTO artworkDTO : artworkDTOs) {
                String artworkId = artworkDTO.getArtworkId();
                check = orderDetailDao.insert(orderId, artworkId, artworkDTO.getPrice());
                if (!check) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Cannot add new order detail").build();
                }
            }
            return Response.ok(artworkDTOs).build();
        } catch (OrderDetailERROR | TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }
}
