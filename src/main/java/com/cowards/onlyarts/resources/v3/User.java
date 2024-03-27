package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource class for managing users in the OnlyArts system. This class provides
 * endpoints for retrieving user information, including fetching all users and
 * getting the top 10 users based on certain criteria.
 *
 * This class interacts with the UserDAO to handle user-related operations.
 */
@Path("/v3/users")
public class User {

    private static final UserDAO userDao = UserDAO.getInstance();

    /**
     * Retrieves all users in the system. This method fetches information about
     * all users registered in the system and returns them as a list.
     *
     * @return Response containing the list of all users.
     */
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

    /**
     * Retrieves the top 10 users based on certain criteria. This method fetches
     * information about the top 10 users based on specific criteria, such as
     * popularity or activity, and returns them as a list.
     *
     * @return Response containing the list of top 10 users.
     */
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
    
    @GET 
    @Path("/role/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllByRole(@PathParam("roleId") String roleId) {
        try {
            List<UserDTO> list = userDao.getAllUserByRole(roleId);
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e).build();
        }
    }
}
