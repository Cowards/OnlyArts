package com.cowards.onlyarts.repositories.report;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code ReportDTO} class represents a data transfer object (DTO) for
 * managing reports on artworks. It contains fields for the report ID, artwork
 * ID, reporter ID, description, report time, and status.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReportDTO {

    /**
     * The ID of the report.
     */
    private String reportId;

    /**
     * The ID of the artwork being reported.
     */
    private String artworkId;

    /**
     * The ID of the user who reported the artwork.
     */
    private String reporterId;

    /**
     * The description or content of the report.
     */
    private String description;

    /**
     * The timestamp indicating when the report was made.
     */
    private Date reportTime;

    /**
     * The status of the report.
     */
    private int status;

    /**
     * Checks if the report has been seen.
     *
     * @return {@code true} if the report has been seen, {@code false}
     * otherwise.
     */
    public boolean isSeen() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the report has been responded.
     *
     * @return {@code true} if the report has been responded, {@code false}
     * otherwise.
     */
    public boolean isResponsed() {
        return (status & 1) == 1;
    }
}
