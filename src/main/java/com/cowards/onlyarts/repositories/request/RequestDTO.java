package com.cowards.onlyarts.repositories.request;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a Data Transfer Object (DTO) for request information.
 * It contains details such as request ID, customer ID, publisher ID, request time,
 * description, and status of the request.
 * <p>
 * The status field is used to represent various states of the request, and utility
 * methods are provided to check if the request has been seen, responded to,
 * approved, or removed.
 * <p>
 * Note: The status is stored as an integer, with each bit representing a different
 * state (seen, responded, approved, removed).
 * <p>
 * This class utilizes Lombok annotations for boilerplate code reduction.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestDTO {

    /**
     * The unique identifier for the request.
     */
    private String requestId;

    /**
     * The identifier of the customer associated with the request.
     */
    private String customerID;

    /**
     * The identifier of the publisher associated with the request.
     */
    private String publisherId;

    /**
     * The timestamp when the request was made.
     */
    private Date requestTime;

    /**
     * A description of the request.
     */
    private String description;

    /**
     * The status of the request, represented as an integer.
     * <p>
     * Bit 0 represents 'removed'.
     * Bit 1 represents 'approved'.
     * Bit 2 represents 'responded'.
     * Bit 3 represents 'seen'.
     */
    private int status;

    /**
     * Checks if the request has been seen.
     *
     * @return true if seen, false otherwise.
     */
    public boolean isSeen() {
        return ((status >> 3) & 1) == 1;
    }

    /**
     * Checks if the request has been responded to.
     *
     * @return true if responded, false otherwise.
     */
    public boolean isResponse() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the request has been approved.
     *
     * @return true if approved, false otherwise.
     */
    public boolean isApproved() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the request has been removed.
     *
     * @return true if removed, false otherwise.
     */
    public boolean isRemoved() {
        return (status & 1) == 1;
    }
}
