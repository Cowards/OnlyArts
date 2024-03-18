package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.repositories.artwork.ArtworkERROR;
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
import java.util.ArrayList;
import java.util.List;

@Path("v2/report/artworks")
public class Report {

    private final ReportDAO reportDAO = ReportDAO.getInstance();
    private final NotificationDAO notificationDAO = NotificationDAO.getInstance();
    private final ArtworkDAO artworkDAO = ArtworkDAO.getInstance();
    private final TokenDAO tokenDAO = TokenDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    @POST
    @Path("/addnewreport")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reportArtwork(@HeaderParam("authtoken") String tokenString, ReportDTO report) {
        try {
            String userId = tokenDAO.getToken(tokenString).getUserId();
            boolean checkSellArtwork = false;
            checkSellArtwork = reportDAO.reportArtwork(userId, report);
            return checkSellArtwork
                    ? Response.status(Response.Status.NO_CONTENT).build()
                    : Response.status(Response.Status.NOT_FOUND).build();
        } catch (TokenERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }

    @GET
    @Path("/showall")
    public Response getAllReports(@HeaderParam("authtoken") String tokenString) throws TokenERROR, UserERROR {
        TokenDTO tokenDto = tokenDAO.getToken(tokenString);
        UserDTO userDTO = userDAO.getUserById(tokenDto.getUserId());
        if (!userDTO.getRoleId().equals("AD")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this page").build();
        }
        List<ReportDTO> listReport = new ArrayList<>();
        listReport = reportDAO.getAllReports();
        if (!listReport.isEmpty()) {
            return Response.ok(listReport, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/handlelist")
    public Response getReportHandleList(@HeaderParam("authtoken") String tokenString) throws UserERROR, TokenERROR {
        TokenDTO tokenDto = tokenDAO.getToken(tokenString);
        UserDTO userDTO = userDAO.getUserById(tokenDto.getUserId());
        if (!userDTO.getRoleId().equals("AD")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this page").build();
        }
        List<ReportDTO> listReport = new ArrayList<>();
        listReport = reportDAO.getReportHandleList();
        if (!listReport.isEmpty()) {
            return Response.ok(listReport, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/processing/{choice}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processReport(@HeaderParam("authtoken") String tokenString, ReportDTO report, @PathParam("choice") String choice) throws ArtworkERROR {
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
                                return Response.status(Response.Status.OK).build();
                            }
                        } else {
                            sendResponseForReporter = notificationDAO.sendResponse(report.getReporterId(),
                                    "Thank you for your report, we had comfirmed that wrong!",
                                    0);
                            if (sendResponseForReporter) {
                                return Response.status(Response.Status.OK).build();
                            }
                        }
                    }
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                throw new UserERROR("You do not have permission to do this action!");
            }
        } catch (TokenERROR | UserERROR ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex).build();
        }
    }
}
