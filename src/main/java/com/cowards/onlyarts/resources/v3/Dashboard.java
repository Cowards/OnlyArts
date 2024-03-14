/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminDashboard(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            UserDTO user = userDAO.getUserById(tokenDTO.getUserId());
            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }
            if (!user.getRoleId().equals("AD")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new UserERROR("You are not allow "
                                + "enter this page"))
                        .build();
            }

            HashMap<String, Object> result = new HashMap();

            List<UserDTO> list = userDAO.getAllUsers();
            List<UserDTO> users = new ArrayList<>();
            for (UserDTO userDto : list) {
                if (!userDto.getRoleId().equals("AD")) {
                    users.add(userDto);
                }
            }
            List<OrderDTO> orders = orderDAO.getAll();
            List<ArtworkDTO> artworks = artworkDAO.getAll();

            result.put("users", users);
            result.put("orders", orders);
            result.put("artworks", artworks);

            return Response.ok(result).build();
        } catch (TokenERROR | UserERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/creator")
    @Produces(MediaType.APPLICATION_JSON)
    public Response creatorDashboard(@HeaderParam("authtoken") String tokenString) {
        try {
            TokenDTO tokenDTO = tokenDao.getToken(tokenString);
            String userId = tokenDTO.getUserId();
            UserDTO user = userDAO.getUserById(userId);

            if (tokenDTO.isExpired()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new TokenERROR("Login timeout"))
                        .build();
            }

            if (!user.getRoleId().equals("CR")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new UserERROR("You are not allow "
                                + "enter this page"))
                        .build();
            }

            HashMap<String, Object> result = new HashMap();

            List<ArtworkDTO> artworks = artworkDAO.getAll(userId);
            List<OrderDTO> orderDTOs = orderDAO.getAllByOwnerId(userId);
            List<UserDTO> userDTOs = userDAO.getFollower(userId);

            result.put("orders", orderDTOs);
            result.put("artworks", artworks);
            result.put("users", userDTOs);
            
            return Response.ok(result).build();
        } catch (TokenERROR | UserERROR e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
