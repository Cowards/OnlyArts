/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.cart;

/**
 * The {@code CartERROR} class represents an exception specific to errors
 * related to cart operations. It extends the {@link java.lang.Exception} class.
 */
public class CartERROR extends Exception {

    /**
     * Constructs a new {@code CartERROR} object with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public CartERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CartERROR} object with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A {@code null} value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public CartERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
