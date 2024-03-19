package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/v3/users")
public class User {

    private static final UserDAO userDao = UserDAO.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        try {
            List<UserDTO> list = userDao.getAllUsers();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }

    @GET
    @Path("/top10")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop10Users() {
        try {
            List<UserDTO> list = userDao.getTop10();
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
