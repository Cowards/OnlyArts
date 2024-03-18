/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.repositories.orderdetail.OrderDetailDTO;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.OrderDetailDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Dat
 */
@Path("/v3/dashboard")
public class Dashboard {

    private static final ArtworkDAO artworkDAO = ArtworkDAO.getInstance();
    private static final UserDAO userDAO = UserDAO.getInstance();
    private static final OrderDAO orderDAO = OrderDAO.getInstance();
    private static final OrderDetailDAO orderDetailDAO = OrderDetailDAO.getInstance();

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        try {
            List<UserDTO> list = userDAO.getAllUsers();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/users/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Users() {
        try {
            List<UserDTO> list = userDAO.getTop10();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/artworks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArtworks() {
        try {
            List<ArtworkDTO> list = artworkDAO.getAll();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/artworks/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Artworks() {
        try {
            List<ArtworkDTO> list = artworkDAO.getTop10();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/orders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDAO.getAll();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails
                        = orderDetailDAO.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork
                            = artworkDAO.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put("artworks", artworks);
            }
            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/orders/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Orders() {
        try {
            HashMap<String, List> result = new HashMap<>();
            List<OrderDTO> orders = orderDAO.getTop10();
            result.put("orders", orders);
            for (OrderDTO order : orders) {
                List<OrderDetailDTO> orderDetails
                        = orderDetailDAO.getAll(order.getOrderId());
                List<ArtworkDTO> artworks = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetails) {
                    ArtworkDTO artwork
                            = artworkDAO.getArtwork(orderDetail.getArtworkId());
                    artworks.add(artwork);
                }
                result.put("artworks", artworks);
            }
            return Response.ok(result).build();
        } catch (ArtworkERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
