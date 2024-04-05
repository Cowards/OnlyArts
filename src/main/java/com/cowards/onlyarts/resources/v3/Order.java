package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.repositories.order.OrderERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.CartDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
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
 * Resource class for handling orders in the OnlyArts system. Provides endpoints
 * for inserting orders, retrieving user's orders, retrieving orders for
 * publishers, retrieving all orders, and retrieving the top 10 orders.
 */
@Path("/v3/orders")
public class Order {

    private static final OrderDAO orderDao = OrderDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();

    /**
     * Inserts a new order into the system.
     *
     * @param orderDTO The order information to be inserted.
     * @param tokenString The authentication token.
     * @return Response containing the inserted order information.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(OrderDTO orderDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            String userId = tokenDTO.getUserId();
            List<ArtworkDTO> artworkDTOs = cartDao.getAll(userId);
            if (artworkDTOs.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("The cart is empty!!!").build();
            }
            String orderId = orderDTO.getOrderId();
            float totalPrice = 0;
            for (ArtworkDTO artworkDTO : artworkDTOs) {
                totalPrice += artworkDTO.getPrice();
            }
            orderDTO.setOrderId(orderId);
            orderDTO.setUserId(userId);
            orderDTO.setTotalPrice(totalPrice);
            boolean check = orderDao.insert(orderDTO);
            if (check) {
                orderDTO = orderDao.getOne(orderId);
                return Response.ok(orderDTO).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You cannot create an order!!!").build();
        } catch (TokenERROR | OrderERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Retrieves all orders placed by the user.
     *
     * @param tokenString The authentication token.
     * @return Response containing the user's orders and associated artwork
     * details.
     */
    @GET
    @Path("/ordered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersForCustomer(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            String userId = tokenDTO.getUserId();
            List<OrderDTO> list = orderDao.getAll(userId);
            return Response.ok(list).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Retrieves all orders received by the publisher (owner).
     *
     * @param tokenString The authentication token.
     * @return Response containing the publisher's orders and associated artwork
     * details.
     */
    @GET
    @Path("/recieved")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersForPublisher(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            String userId = tokenDTO.getUserId();
            List<OrderDTO> list = orderDao.getAllByOwnerId(userId);
            return Response.ok(list).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Retrieves all orders in the system.
     *
     * @param tokenString The authentication token.
     * @return Response containing all orders and associated artwork details.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            UserDTO userDTO = userDao.getUserById(tokenDTO.getUserId());
            if (!userDTO.getRoleId().equals("AD")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You dont have permission to do this action!!!")
                        .build();
            }
            List<OrderDTO> list = orderDao.getAll();
            return Response.ok(list).build();
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }

    /**
     * Retrieves the top 10 orders based on some criteria.
     *
     * @param tokenString The authentication token.
     * @return Response containing the top 10 orders and associated artwork
     * details.
     */
    @GET
    @Path("/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Orders(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            UserDTO userDTO = userDao.getUserById(tokenDTO.getUserId());
            if (!userDTO.getRoleId().equals("AD")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You dont have permission to do this action!!!")
                        .build();
            }
            List<OrderDTO> list = orderDao.getTop10();
            return Response.ok(list).build();
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }

    /**
     * Deletes an order from the system.
     *
     * @param orderDTO
     * @return Response indicating the success or failure of the deletion
     * operation.
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(OrderDTO orderDTO) {
        try {
            String orderId = orderDTO.getOrderId();
            boolean check = orderDao.remove(orderId);
            orderDTO = orderDao.getOne(orderId);
            if (check) {
                return Response.ok(orderDTO).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot remove this order!!!").build();
        } catch (OrderERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex).build();
        }
    }
}
