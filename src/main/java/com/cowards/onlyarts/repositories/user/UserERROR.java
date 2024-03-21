package com.cowards.onlyarts.repositories.user;

/**
 * The {@code UserERROR} class represents an exception specific to errors
 * related to user operations. It extends the {@link java.lang.Exception} class.
 */
public class UserERROR extends Exception {

    /**
     * Constructs a new {@code UserERROR} object with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public UserERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UserERROR} object with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A {@code null} value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public UserERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
