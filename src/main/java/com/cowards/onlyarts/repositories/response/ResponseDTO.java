package com.cowards.onlyarts.repositories.response;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code ResponseDTO} class represents a data transfer object (DTO) for
 * managing responses to requests. It contains fields for the response ID,
 * request ID, image, description, response time, and status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseDTO {

    /**
     * The ID of the response.
     */
    private String responseId;

    /**
     * The ID of the request to which the response belongs.
     */
    private String requestId;

    /**
     * The image associated with the response.
     */
    private String image;

    /**
     * The description or content of the response.
     */
    private String description;

    /**
     * The timestamp indicating when the response was made.
     */
    private Date responseTime;

    /**
     * The status of the response.
     */
    private int status; //(seen/remove)

    /**
     * Checks if the response has been removed.
     *
     * @return {@code true} if the response has been removed, {@code false}
     * otherwise.
     */
    public boolean isRemoved() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the response has been seen.
     *
     * @return {@code true} if the response has been seen, {@code false}
     * otherwise.
     */
    public boolean isSeen() {
        return (status & 1) == 1;
    }
}
