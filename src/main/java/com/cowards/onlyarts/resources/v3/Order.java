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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
@Path("/v3/orders")
public class Order {

    private static final OrderDAO orderDao = OrderDAO.getInstance();
    private static final OrderDetailDAO orderDetailDao = OrderDetailDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final CartDAO cartDao = CartDAO.getInstance();
    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(OrderDTO orderDTO,
            @HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            String userId = tokenDTO.getUserId();
            List<CartDTO> cartDTOs = cartDao.getAll(userId);
            if (cartDTOs.isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }
            HashMap<String, Object> result = new HashMap();

            orderDTO.setOrderId(CodeGenerator.generateUUID(20));
            orderDTO.setUserId(userId);
            orderDTO.setStatus(0);
            if (!orderDao.insert(orderDTO)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }

            String orderId = orderDTO.getOrderId();
            orderDTO = orderDao.getOne(orderId);

            result.put("order", orderDTO);
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setOrderId(orderId);
            for (CartDTO cartDTO : cartDTOs) {
                String artworkId = cartDTO.getArtworkId();
                orderDetailDTO.setArtworkId(artworkId);
                if (!orderDetailDao.insert(orderDetailDTO)) {
                    return Response.status(Response.Status.NOT_ACCEPTABLE)
                            .build();
                }
                ArtworkDTO artworkDTO = artworkDao.getArtwork(artworkId);
                result.put(artworkId, artworkDTO);
            }

            if (!cartDao.delete(userId)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }

            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        } catch (OrderERROR | OrderDetailERROR ex) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(ex).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex).build();
        }
    }

    @GET
    @Path("/status")
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
                List<OrderDetailDTO> orderDetailDTOs = orderDetailDao.getAll(orderId);
                List<ArtworkDTO> artworkDTOs = new ArrayList<>();
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOs) {
                    ArtworkDTO artworkDTO = artworkDao.getArtwork(orderDetailDTO.getArtworkId());
                    artworkDTOs.add(artworkDTO);
                }

                result.put(orderId, artworkDTOs);
            }

            result.put("orders", orderDTOs);

            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/owner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersForPublisher(@HeaderParam("authtoken") String tokenString
    ) {
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
                List<OrderDetailDTO> orderDetails = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork = artworkDao.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put(order.getOrderId(), artworks);
            }
            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDao.getAll();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork = artworkDao.getArtwork(orderDetail.getArtworkId());
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

    @GET
    @Path("/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Orders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDao.getTop10();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails = orderDetailDao.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork = artworkDao.getArtwork(orderDetail.getArtworkId());
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
