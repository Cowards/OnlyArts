package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.request.RequestDTO;
import com.cowards.onlyarts.repositories.request.RequestERROR;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.RequestDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource class for managing requests in the OnlyArts system. This class
 * provides endpoints for retrieving, creating, updating, and deleting requests,
 * as well as approving, rejecting, and handling request status updates.
 *
 * This class interacts with the RequestDAO, TokenDAO, and UserDAO to handle
 * operations related to requests.
 */
@Path("v4/requests")
public class Request {

    private final RequestDAO requestDAO = RequestDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    /**
     * Endpoint for retrieving all requests. This method returns a list of all
     * requests based on the authenticated user's role.
     *
     * @param tokenString The authentication token.
     * @return Response containing the list of requests.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRequests(@HeaderParam("authtoken") String tokenString) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            String roleID = userDAO.getUserById(userId).getRoleId();
            if ("CT".equalsIgnoreCase(roleID) || "CR".equalsIgnoreCase(roleID)) {
                List<RequestDTO> requestList = requestDAO.getAllRequest(userId, roleID);
                return !requestList.isEmpty()
                        ? Response.status(Response.Status.OK).entity(requestList).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new UserERROR("You do not have permission");
            }
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving a request by ID. This method returns the request
     * details specified by the request ID.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to retrieve.
     * @return Response containing the request details.
     */
    @GET
    @Path("{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequestByID(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getCustomerID()) || userId.equalsIgnoreCase(request.getPublisherId())) {
                return Response.status(Response.Status.OK)
                        .entity(request)
                        .build();
            } else {
                throw new TokenERROR("You do not have permission to see this request");
            }

        } catch (TokenERROR | RequestERROR ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex)
                    .build();
        }
    }

    /**
     * Endpoint for creating a new request. This method creates a new request
     * based on the provided request data.
     *
     * @param tokenString The authentication token.
     * @param request The request data to create.
     * @return Response indicating success or failure of the operation.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRequest(@HeaderParam("authtoken") String tokenString,
            RequestDTO request) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            UserDTO user = userDAO.getUserById(userId);
            if ("CT".equalsIgnoreCase(user.getRoleId())) {
                String requestId = CodeGenerator.generateUUID(20);
                request.setCustomerID(userId);
                request.setRequestId(requestId);
                boolean checkInsert = requestDAO.addRequest(request);
                if (checkInsert) {
                    request = requestDAO.getRequestById(requestId);
                }
                return checkInsert
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new UserERROR("Only customer can request");
            }
        } catch (TokenERROR | UserERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for updating an existing request. This method updates the
     * details of an existing request based on the provided request data.
     *
     * @param tokenString The authentication token.
     * @param request The updated request data.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRequest(@HeaderParam("authtoken") String tokenString,
            RequestDTO request) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            if (userId.equalsIgnoreCase(request.getCustomerID())) {
                boolean checkUpdate = requestDAO.updateRequest(request);
                if (checkUpdate) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return checkUpdate
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new TokenERROR("You do not have permission to update this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for deleting a request. This method deletes the specified
     * request.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to delete.
     * @return Response indicating success or failure of the operation.
     */
    @DELETE
    @Path("{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRequest(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getCustomerID())) {
                boolean checkDelete = requestDAO.removeRequest(requestId);
                if (checkDelete) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return checkDelete
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new RequestERROR("You do not have permission to delete this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for approving a request. This method approves the specified
     * request.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to approve.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("approve/{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveRequest(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getPublisherId())) {
                boolean check = requestDAO.changeStatus(request, 0b0010);
                if (check) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new RequestERROR("You do not have permission to approve/reject this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for marking a request as seen. This method marks the specified
     * request as seen.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to mark as seen.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("seen/{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readRequest(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getPublisherId())) {
                boolean check = requestDAO.changeStatus(request, 0b1000);
                if (check) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new RequestERROR("You do not have permission to seen/unseen this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for responding to a request. This method responds to the
     * specified request.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to respond to.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("response/{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectRequest(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getPublisherId())) {
                boolean check = requestDAO.changeStatus(request, 0b0100);
                if (check) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new RequestERROR("You do not have permission to response this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }

    /**
     * Endpoint for removing a request. This method removes the specified
     * request.
     *
     * @param tokenString The authentication token.
     * @param requestId The ID of the request to remove.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("remove/{request_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doneRequest(@HeaderParam("authtoken") String tokenString,
            @PathParam("request_id") String requestId) {
        try {
            String userId = tokenDao.getToken(tokenString).getUserId();
            RequestDTO request = requestDAO.getRequestById(requestId);
            if (userId.equalsIgnoreCase(request.getPublisherId())) {
                if (request.isApproved() || request.isResponse()) {
                    throw new RequestERROR("Resquest has been approved/responded. Cannot remove!");
                }
                boolean check = requestDAO.changeStatus(request, 0b0001);
                if (check) {
                    request = requestDAO.getRequestById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(request).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new RequestERROR("You do not have permission to remove this request");
            }
        } catch (TokenERROR | RequestERROR e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e)
                    .build();
        }
    }
}
