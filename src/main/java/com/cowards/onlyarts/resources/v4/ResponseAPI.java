package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.repositories.request.RequestDTO;
import com.cowards.onlyarts.repositories.request.RequestERROR;
import com.cowards.onlyarts.repositories.response.ResponseDTO;
import com.cowards.onlyarts.repositories.response.ResponseERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.RequestDAO;
import com.cowards.onlyarts.services.ResponseDAO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * The ResponseAPI class provides endpoints for managing responses in the
 * OnlyArts system. This class allows users to add, retrieve, remove, and mark
 * responses as seen.
 */
@Path("v4/response")
public class ResponseAPI {

    private final RequestDAO requestDAO = RequestDAO.getInstance();
    private final TokenDAO tokenDAO = TokenDAO.getInstance();
    private final ResponseDAO responseDAO = ResponseDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    /**
     * Endpoint for adding a response. This method adds a response to a request.
     *
     * @param tokenString The authentication token.
     * @param responseDTO The response data to add.
     * @return Response indicating success or failure of the operation.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addResponse(@HeaderParam("authtoken") String tokenString, ResponseDTO responseDTO) {
        try {
            TokenDTO user = tokenDAO.getToken(tokenString);
            RequestDTO request = requestDAO.getRequestById(responseDTO.getRequestId());
            if (request.getPublisherId().equalsIgnoreCase(user.getUserId())) {
                boolean check = responseDAO.addResponse(responseDTO);
                if (check) {
                    responseDTO = responseDAO.getResponseById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(responseDTO).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new ResponseERROR("You can not response for this request");
            }

        } catch (TokenERROR | RequestERROR | ResponseERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving all responses. This method retrieves all
     * responses based on the user's role.
     *
     * @param tokenString The authentication token.
     * @return Response containing the list of responses.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllResponsesById(@HeaderParam("authtoken") String tokenString) {
        try {
            List<ResponseDTO> responseList = new ArrayList<>();
            TokenDTO user = tokenDAO.getToken(tokenString);
            UserDTO loginUser = userDAO.getUserById(user.getUserId());
            if ("CR".equalsIgnoreCase(loginUser.getRoleId())) {
                responseList = responseDAO.getAllResponseById(user.getUserId());
            } else if ("CT".equalsIgnoreCase(loginUser.getRoleId())) {
                responseList = responseDAO.getAllResponseByCustomerId(user.getUserId());
            } else {
                throw new ResponseERROR("You do not have permission to view response");
            }
            return !responseList.isEmpty()
                    ? Response.status(Response.Status.OK).entity(responseList).build()
                    : Response.status(Response.Status.NO_CONTENT).build();
        } catch (TokenERROR | UserERROR | ResponseERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving a response by ID. This method retrieves a
     * response based on its ID.
     *
     * @param responseId The ID of the response to retrieve.
     * @return Response containing the retrieved response.
     */
    @GET
    @Path("{responseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResponseById(@PathParam("responseId") String responseId) {
        try {
            ResponseDTO response = responseDAO.getResponseById(responseId);
            return response != null
                    ? Response.status(Response.Status.OK).entity(response).build()
                    : Response.status(Response.Status.NO_CONTENT).build();
        } catch (ResponseERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

    /**
     * Endpoint for removing a response by ID. This method removes a response
     * based on its ID.
     *
     * @param tokenString The authentication token.
     * @param responseId The ID of the response to remove.
     * @return Response indicating success or failure of the operation.
     */
    @DELETE
    @Path("{responseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeResponseById(@HeaderParam("authtoken") String tokenString,
            @PathParam("responseId") String responseId) {
        try {
            TokenDTO user = tokenDAO.getToken(tokenString);
            ResponseDTO response = responseDAO.getResponseById(responseId);
            RequestDTO request = requestDAO.getRequestById(response.getRequestId());
            if (user.getUserId().equalsIgnoreCase(request.getPublisherId())) {
                boolean check = responseDAO.changeStatus(response, 0b10);
                if (check) {
                    response = responseDAO.getResponseById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(response).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new ResponseERROR("You can not delete this response");
            }
        } catch (TokenERROR | ResponseERROR | RequestERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

    /**
     * Endpoint for marking a response as seen. This method marks a response as
     * seen by the customer.
     *
     * @param tokenString The authentication token.
     * @param responseId The ID of the response to mark as seen.
     * @return Response indicating success or failure of the operation.
     */
    @PUT
    @Path("seen/{responseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response seenResponse(@HeaderParam("authtoken") String tokenString,
            @PathParam("responseId") String responseId) {
        try {
            TokenDTO user = tokenDAO.getToken(tokenString);
            ResponseDTO response = responseDAO.getResponseById(responseId);
            RequestDTO request = requestDAO.getRequestById(response.getRequestId());
            if (user.getUserId().equalsIgnoreCase(request.getCustomerID())) {
                boolean check = responseDAO.changeStatus(response, 0b01);
                if (check) {
                    response = responseDAO.getResponseById(request.getRequestId());
                }
                return check
                        ? Response.status(Response.Status.OK).entity(response).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            } else {
                throw new ResponseERROR("You can not seen this response");
            }
        } catch (TokenERROR | ResponseERROR | RequestERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex).build();
        }
    }

}
