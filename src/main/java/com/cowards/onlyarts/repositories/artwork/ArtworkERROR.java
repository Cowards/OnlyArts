package com.cowards.onlyarts.repositories.artwork;

/**
 * The {@code ArtworkERROR} class represents an exception specific to errors
 * related to artwork operations. It extends the {@link java.lang.Exception}
 * class.
 */
public class ArtworkERROR extends Exception {

    /**
     * Constructs a new {@code ArtworkERROR} object with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public ArtworkERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ArtworkERROR} object with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A {@code null} value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public ArtworkERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
