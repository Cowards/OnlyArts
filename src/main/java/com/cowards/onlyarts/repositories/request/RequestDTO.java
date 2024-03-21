package com.cowards.onlyarts.repositories.request;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code RequestDTO} class represents a data transfer object (DTO) for
 * managing requests between customers and publishers. It contains fields for
 * the request ID, customer ID, publisher ID, request time, description, and
 * status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestDTO {

    /**
     * The ID of the request.
     */
    private String requestId;

    /**
     * The ID of the customer making the request.
     */
    private String customerID;

    /**
     * The ID of the publisher who receives the request.
     */
    private String publisherId;

    /**
     * The timestamp indicating when the request was made.
     */
    private Date requestTime;

    /**
     * The description or content of the request.
     */
    private String description;

    /**
     * The status of the request.
     */
    private int status;

    /**
     * Checks if the request has been seen.
     *
     * @return {@code true} if the request has been seen, {@code false}
     * otherwise.
     */
    public boolean isSeen() {
        return ((status >> 3) & 1) == 1;
    }

    /**
     * Checks if the request has been responded to.
     *
     * @return {@code true} if the request has been responded to, {@code false}
     * otherwise.
     */
    public boolean isResponse() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the request has been approved.
     *
     * @return {@code true} if the request has been approved, {@code false}
     * otherwise.
     */
    public boolean isApproved() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the request has been removed.
     *
     * @return {@code true} if the request has been removed, {@code false}
     * otherwise.
     */
    public boolean isRemoved() {
        return (status & 1) == 1;
    }
}
