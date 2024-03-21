package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.cart.CartDTO;
import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.repositories.order.OrderERROR;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailDTO;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CartDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Resource class for handling orders in the OnlyArts system. Provides endpoints
 * for inserting orders, retrieving user's orders, retrieving orders for
 * publishers, retrieving all orders, and retrieving the top 10 orders.
 */
@Path("/v3/orders")
public class Order {

    private static final OrderDAO orderDao = OrderDAO.getInstance();
    private static final OrderDetailDAO orderDetailDao = OrderDetailDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();

    /**
     * Inserts a new order into the system. This method retrieves cart items
     * associated with the user, calculates the total price, deletes the cart
     * items, inserts the order into the database along with order details, and
     * returns the order information.
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
            List<CartDTO> cartDTOs = cartDao.getAll(userId);
            if (cartDTOs.isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }
            HashMap<String, Object> result = new HashMap();
            List<ArtworkDTO> artworkDTOs = new ArrayList<>();
            String orderId = CodeGenerator.generateUUID(20);
            float totalPrice = 0;
            for (CartDTO cartDTO : cartDTOs) {
                String artworkId = cartDTO.getArtworkId();
                ArtworkDTO artworkDTO = artworkDao.getArtwork(artworkId);
                totalPrice += artworkDTO.getPrice();
                artworkDTOs.add(artworkDTO);
            }
            result.put("artworks", artworkDTOs);
            if (!cartDao.delete(userId)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }

            orderDTO.setTotalPrice(totalPrice);
            orderDTO.setOrderId(orderId);
            orderDTO.setUserId(userId);
            orderDTO.setStatus(0);
            if (!orderDao.insert(orderDTO)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }
            orderDTO = orderDao.getOne(orderId);
            result.put("order", orderDTO);

            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setOrderId(orderId);
            for (ArtworkDTO artworkDTO : artworkDTOs) {
                String artworkId = artworkDTO.getArtworkId();
                orderDetailDTO.setArtworkId(artworkId);
                if (!orderDetailDao.insert(orderDetailDTO)) {
                    return Response.status(Response.Status.NOT_ACCEPTABLE)
                            .build();
                }
            }

            return Response.ok(result).build();
        } catch (OrderDetailERROR | TokenERROR | ArtworkERROR | OrderERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Retrieves all orders placed by the user. This method returns a list of
     * orders along with their associated artwork details.
     *
     * @param tokenString The authentication token.
     * @return Response containing the user's orders and associated artwork
     * details.
     */
    @GET
    @Path("/ordered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            String userId = tokenDTO.getUserId();
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            HashMap<String, Object> result = new HashMap<>();

            List<OrderDTO> orderDTOs = orderDao.getAll(userId);
            for (OrderDTO orderDTO : orderDTOs) {
                String orderId = orderDTO.getOrderId();
                List<OrderDetailDTO> orderDetailDTOs
                        = orderDetailDao.getAll(orderId);
                List<ArtworkDTO> artworkDTOs
                        = new ArrayList<>();
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOs) {
                    ArtworkDTO artworkDTO
                            = artworkDao.getArtwork(orderDetailDTO.getArtworkId());
                    artworkDTOs.add(artworkDTO);
                }

                result.put(orderId, artworkDTOs);
            }

            result.put("orders", orderDTOs);

            return Response.ok(result).build();
        } catch (TokenERROR | ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Retrieves all orders received by the publisher (owner). This method
     * returns a list of orders along with their associated artwork details.
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
            String userId = tokenDTO.getUserId();
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            HashMap<String, List> result = new HashMap<>();

            List<OrderDTO> orders = orderDao.getAllByOwnerId(userId);
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails
                        = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork
                            = artworkDao.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put(order.getOrderId(), artworks);
            }
            return Response.ok(result).build();
        } catch (TokenERROR | ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Retrieves all orders in the system. This method returns a list of orders
     * along with their associated artwork details.
     *
     * @return Response containing all orders and associated artwork details.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDao.getAll();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails
                        = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork
                            = artworkDao.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put(order.getOrderId(), artworks);
            }
            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    /**
     * Retrieves the top 10 orders based on some criteria. This method returns a
     * list of orders along with their associated artwork details.
     *
     * @return Response containing the top 10 orders and associated artwork
     * details.
     */
    @GET
    @Path("/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Orders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDao.getTop10();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails
                        = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork
                            = artworkDao.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put(order.getOrderId(), artworks);
            }
            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
