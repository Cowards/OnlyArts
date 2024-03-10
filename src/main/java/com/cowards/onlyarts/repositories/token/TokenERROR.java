package com.cowards.onlyarts.repositories.token;

/**
 * Class representing an error related to tokens
 */
public class TokenERROR extends Exception {

    /**
     * Constructs a TokenError with the specified error message.
     *
     * @param message The error message.
     */
    public TokenERROR(String message) {
        super(message);
    }

    /**
     * Constructs a TokenError with the specified error message and a cause.
     *
     * @param message The error message.
     * @param cause The cause of the error.
     */
    public TokenERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
