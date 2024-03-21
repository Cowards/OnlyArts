package com.cowards.onlyarts.repositories.response;

/**
 * Custom exception class for handling errors related to responses.
 */
public class ResponseERROR extends Exception {

    /**
     * Constructs a new ResponseERROR with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public ResponseERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new ResponseERROR with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public ResponseERROR(String message, Throwable cause) {
        super(message, cause);
    }
}