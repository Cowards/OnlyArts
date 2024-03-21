/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.orderdetail;

/**
 * The {@code OrderDetailERROR} class represents an exception specific to errors
 * related to order details. It extends the {@link java.lang.Exception} class.
 */
public class OrderDetailERROR extends Exception {

    /**
     * Constructs a new {@code OrderDetailERROR} object with the specified
     * detail message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     */
    public OrderDetailERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code OrderDetailERROR} object with the specified
     * detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A {@code null} value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public OrderDetailERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
