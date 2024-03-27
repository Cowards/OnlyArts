package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
import com.cowards.onlyarts.repositories.notification.NotificationDTO;
import com.cowards.onlyarts.repositories.report.ReportDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.NotificationDAO;
import com.cowards.onlyarts.services.ReportDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the endpoints for managing reports, including reporting
 * artworks, retrieving all reports, and processing reports.
 */
@Path("v2/reports")
public class Report {

    private final ReportDAO reportDAO = ReportDAO.getInstance();
    private final NotificationDAO notificationDAO = NotificationDAO.getInstance();
    private final ArtworkDAO artworkDAO = ArtworkDAO.getInstance();
    private final TokenDAO tokenDAO = TokenDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    /**
     * Endpoint for reporting an artwork.
     *
     * @param tokenString The authentication token.
     * @param report The report data.
     * @return Response indicating success or failure of reporting the artwork.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reportArtwork(@HeaderParam("authtoken") String tokenString,
            ReportDTO report) {
        try {
            String userId = tokenDAO.getToken(tokenString).getUserId();
            String reportId = CodeGenerator.generateUUID(20);
            report.setReportId(reportId);
            report.setReporterId(userId);
            boolean checkSellArtwork = reportDAO.reportArtwork(report);
            return checkSellArtwork
                    ? Response.status(Response.Status.OK)
                            .entity(reportDAO.getReport(reportId)).build()
                    : Response.status(Response.Status.NOT_FOUND).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }

    /**
     * Endpoint for retrieving all reports.
     *
     * @param tokenString The authentication token.
     * @return Response containing a list of all reports.
     * @throws TokenERROR If there's an error with the authentication token.
     * @throws UserERROR If the user does not have permission to access this
     * endpoint.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReports(@HeaderParam("authtoken") String tokenString)
            throws TokenERROR, UserERROR {
        TokenDTO tokenDto = tokenDAO.getToken(tokenString);
        UserDTO userDTO = userDAO.getUserById(tokenDto.getUserId());
        if (!userDTO.getRoleId().equals("AD")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You cannot access this page").build();
        }
        List<ReportDTO> listReport = reportDAO.getAllReports();
        return Response.ok(listReport).build();
    }

    /**
     * Endpoint for processing a report.
     *
     * @param tokenString The authentication token.
     * @param report The report data.
     * @param choice The choice for processing the report (Accepted/Rejected).
     * @return Response indicating success or failure of processing the report.
     * @throws ArtworkERROR If there's an error with the artwork related to the
     * report.
     */
    @POST
    @Path("/processing/{choice}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processReport(@HeaderParam("authtoken") String tokenString,
            ReportDTO report, @PathParam("choice") String choice) throws ArtworkERROR {
        try {
            UserDTO currentUser = userDAO.getUserById(tokenDAO.getToken(tokenString).getUserId());
            if (currentUser.getRoleId().equals("AD")) {
                boolean processReport = false;
                boolean writeDiary = false;
                boolean sendResponseForReporter = false;
                boolean sendResponseForUser = false;
                processReport = reportDAO.processReport(currentUser.getUserId(), report, choice);
                if (processReport) {
                    writeDiary = reportDAO.writeDiary(currentUser.getUserId(), report.getReportId(), choice);
                    if (writeDiary) {
                        if (choice.equalsIgnoreCase("Accepted")) {
                            sendResponseForReporter = notificationDAO.sendResponse(report.getReporterId(),
                                    "Thank you for your report, we had comfirmed that right!",
                                    0);
                            sendResponseForUser = notificationDAO.sendResponse(artworkDAO.getArtwork(report.getArtworkId()).getOwnerId(),
                                    "Warning: You are reported by someone, because of Copyright problem",
                                    0);
                            if (sendResponseForReporter && sendResponseForUser) {
                                return Response.status(Response.Status.OK)
                                        .entity(report).build();
                            }
                        } else {
                            sendResponseForReporter = notificationDAO.sendResponse(report.getReporterId(),
                                    "Thank you for your report, we had comfirmed that wrong!",
                                    0);
                            if (sendResponseForReporter) {
                                return Response.status(Response.Status.OK)
                                        .entity(report).build();
                            }
                        }
                    }
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                throw new UserERROR("You do not have permission to do this action!");
            }
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(Response.Status.FORBIDDEN).entity(ex).build();
        }
    }
}
