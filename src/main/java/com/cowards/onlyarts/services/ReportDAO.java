package com.cowards.onlyarts.services;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.core.DBContext;
import com.cowards.onlyarts.repositories.report.ReportDTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides data access operations for managing reports on artworks.
 */
public class ReportDAO {

    private static ReportDAO instance;
    private static final DBContext DB = DBContext.getInstance();

    private static final String ADD_REPORT
            = "INSERT INTO Reports (report_id, artwork_id, reporter_id, description, status) "
            + "VALUES (?, ?, ?, ?, 0)";
    private static final String GET_ALL_REPORTS
            = "SELECT report_id, artwork_id, reporter_id, description, report_time, status FROM  Reports";
    private static final String GET_REPORT_HANDLE
            = "SELECT report_id, artwork_id, reporter_id, description, report_time, status FROM  Reports WHERE [status] = 0";
    private static final String PROCESS_REPORT
            = "UPDATE Reports SET status = ? WHERE report_id = ?";
    private static final String STORE_DIARY_REPORT
            = "INSERT INTO Reports_handler (report_id, mod_id, description) VALUES (?, ?, ?)";

    private static void logError(String message, Throwable ex) {
        Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, message, ex);
    }

    /**
     * Private constructor for the ReportDAO class. Prevents instantiation from
     * outside the class.
     */
    private ReportDAO() {
    }

    /**
     * Gets the instance of ReportDAO using the singleton pattern. If the
     * instance is null, a new instance is created.
     *
     * @return The instance of ReportDAO.
     */
    public static ReportDAO getInstance() {
        if (instance == null) {
            instance = new ReportDAO();
        }
        return instance;
    }

    /**
     * Reports an artwork based on the provided information.
     *
     * @param userId The ID of the user who is reporting the artwork.
     * @param report The ReportDTO object containing the report details.
     * @return True if the artwork is reported successfully; otherwise, false.
     */
    public boolean reportArtwork(String userId, ReportDTO report) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(ADD_REPORT);
                stm.setString(1, CodeGenerator.generateUUID(20));
                stm.setString(2, report.getArtworkId());
                stm.setString(3, userId);
                stm.setString(4, report.getDescription());
                check = stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logError("Exception found on reportArtwork() method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Retrieves a list of all reports on artworks.
     *
     * @return A list of ReportDTO objects containing information about each
     * report.
     */
    public List<ReportDTO> getAllReports() {
        List<ReportDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(GET_ALL_REPORTS);
                rs = stm.executeQuery();

                while (rs.next()) {
                    String reportId = rs.getString("report_id");
                    String artworkId = rs.getString("artwork_id");
                    String reporterId = rs.getString("reporter_id");
                    String description = rs.getString("description");
                    Date reportTime = rs.getDate("report_time");
                    int status = rs.getInt("status");
                    list.add(new ReportDTO(reportId, artworkId, reporterId, description, reportTime, status));
                }
            }
        } catch (SQLException e) {
            logError("Exception found on getAllReports() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    /**
     * Retrieves a list of reports that need to be handled by administrators.
     *
     * @return A list of ReportDTO objects containing information about each
     * report.
     */
    public List<ReportDTO> getReportHandleList() {
        List<ReportDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(GET_REPORT_HANDLE);
                rs = stm.executeQuery();

                while (rs.next()) {
                    String reportId = rs.getString("report_id");
                    String artworkId = rs.getString("artwork_id");
                    String reporterId = rs.getString("reporter_id");
                    String description = rs.getString("description");
                    Date reportTime = rs.getDate("report_time");
                    int status = rs.getInt("status");
                    list.add(new ReportDTO(reportId, artworkId, reporterId, description, reportTime, status));
                }
            }
        } catch (SQLException e) {
            logError("Exception found on getAllReports() method", e);
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stm);
        }
        return list;
    }

    /**
     * Processes a report by updating its status based on the choice made by a
     * moderator.
     *
     * @param modId The ID of the moderator processing the report.
     * @param report The ReportDTO object containing information about the
     * report.
     * @param choice The choice made by the moderator ("Accepted" or
     * "Rejected").
     * @return True if the report processing is successful, false otherwise.
     */
    public boolean processReport(String modId, ReportDTO report, String choice) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        int status = 0;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                if (choice.equalsIgnoreCase("Accepted")) {
                    status = 6;
                } else if (choice.equalsIgnoreCase("Rejected")) {
                    status = 5;
                }
                stm = conn.prepareStatement(PROCESS_REPORT);
                stm.setInt(1, status);
                stm.setString(2, report.getReportId());
                check = stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logError("Exception found on processReport() method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }

    /**
     * Writes a diary entry associated with a report.
     *
     * @param modId The ID of the moderator writing the diary entry.
     * @param reportId The ID of the report associated with the diary entry.
     * @param description The description or content of the diary entry.
     * @return True if the diary entry is successfully written, false otherwise.
     */
    public boolean writeDiary(String modId, String reportId, String description) {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean check = false;
        try {
            conn = DB.getConnection();
            if (conn != null) {
                stm = conn.prepareStatement(STORE_DIARY_REPORT);
                stm.setString(1, reportId);
                stm.setString(2, modId);
                stm.setString(3, description);
                check = stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logError("Exception found on writeDiary() method", e);
        } finally {
            DB.closeStatement(stm);
        }
        return check;
    }
}
