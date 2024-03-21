package com.cowards.onlyarts.repositories.request;

/**
 * Custom exception class for handling errors related to requests.
 */
public class RequestERROR extends Exception {

    /**
     * Constructs a new RequestERROR with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public RequestERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new RequestERROR with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public RequestERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
