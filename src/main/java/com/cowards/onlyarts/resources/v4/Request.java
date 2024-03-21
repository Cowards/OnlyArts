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

/*
 * This class defines RESTful endpoints related to requests for artwork.
 */
@Path("v4/requests")
public class Request {

    private final RequestDAO requestDAO = RequestDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    /**
     * Retrieves all requests associated with the authenticated user.
     *
     * @param tokenString The authentication token of the user.
     * @return Response containing the list of requests in JSON format if successful,
     *         or an error response if the user is unauthorized or no requests are found.
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
     * Retrieves a specific request by its ID.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to retrieve.
     * @return Response containing the requested request in JSON format if successful,
     *         or an error response if the request is not found or the user is unauthorized.
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
     * Creates a new request.
     *
     * @param tokenString The authentication token of the user.
     * @param request     The request details.
     * @return Response indicating success or failure of the request creation.
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
     * Updates an existing request.
     *
     * @param tokenString The authentication token of the user.
     * @param request     The updated request details.
     * @return Response indicating success or failure of the request update.
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
     * Deletes a request.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to delete.
     * @return Response indicating success or failure of the request deletion.
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
     * Approves a request.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to approve.
     * @return Response indicating success or failure of the request approval.
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
     * Marks a request as seen.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to mark as seen.
     * @return Response indicating success or failure of marking the request as seen.
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
     * Responds to a request.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to respond to.
     * @return Response indicating success or failure of responding to the request.
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
     * Remove a request.
     *
     * @param tokenString The authentication token of the user.
     * @param requestId   The ID of the request to mark as done.
     * @return Response indicating success or failure of marking the request as done.
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
