package com.cowards.onlyarts.repositories.response;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a Data Transfer Object (DTO) for response information.
 * It contains details such as response ID, request ID, image, description, response time,
 * and status of the response.
 * <p>
 * The status field is used to represent whether the response has been seen or removed.
 * Utility methods are provided to check if the response has been seen or removed.
 * <p>
 * Note: The status is stored as an integer, where bit 0 represents 'seen' and bit 1 represents 'removed'.
 * <p>
 * This class utilizes Lombok annotations for boilerplate code reduction.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseDTO {

    /**
     * The unique identifier for the response.
     */
    private String responseId;

    /**
     * The identifier of the request associated with the response.
     */
    private String requestId;

    /**
     * The image associated with the response.
     */
    private String image;

    /**
     * A description of the response.
     */
    private String description;

    /**
     * The timestamp when the response was made.
     */
    private Date responseTime;

    /**
     * The status of the response, represented as an integer.
     * <p>
     * Bit 0 represents 'seen'.
     * Bit 1 represents 'removed'.
     */
    private int status;

    /**
     * Checks if the response has been removed.
     *
     * @return true if removed, false otherwise.
     */
    public boolean isRemoved() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the response has been seen.
     *
     * @return true if seen, false otherwise.
     */
    public boolean isSeen() {
        return (status & 1) == 1;
    }
}
