/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.request;

/**
 * The {@code RequestERROR} class represents an exception specific to errors
 * related to requests between customers and publishers. It extends the
 * {@link java.lang.Exception} class.
 */
public class RequestERROR extends Exception {

    /**
     * Constructs a new {@code RequestERROR} object with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public RequestERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code RequestERROR} object with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A {@code null} value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public RequestERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
