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
import java.util.HashMap;
import java.util.List;

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
    @Path("/create")
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

            orderDTO.setOrderId(CodeGenerator.generateUUID(20));
            orderDTO.setUserId(userId);
            orderDTO.setStatus(0);
            if (!orderDao.insert(orderDTO)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }
            String orderId = orderDTO.getOrderId();
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
                ArtworkDTO artworkDTO = artworkDao.getOne(artworkId);
                result.put(artworkId, artworkDTO);
            }

            if (!cartDao.delete(userId)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .build();
            }

            return Response.ok(result).build();
        } catch (OrderDetailERROR | TokenERROR | ArtworkERROR | OrderERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
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
                List<OrderDetailDTO> orderDetailDTOs
                        = orderDetailDao.getAll(orderId);
                result.put(orderId, orderDetailDTOs);
            }

            result.put("orders", orderDTOs);

            return Response.ok(result).build();
        } catch (TokenERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }
}
