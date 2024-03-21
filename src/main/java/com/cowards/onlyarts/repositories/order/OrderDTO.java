/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.order;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code OrderDTO} class represents a data transfer object (DTO) for
 * managing orders. It contains fields for the order ID, user ID, status,
 * payment method, order time, and total price.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {

    /**
     * The ID of the order.
     */
    private String orderId;

    /**
     * The ID of the user who placed the order.
     */
    private String userId;

    /**
     * The status of the order.
     */
    private int status;

    /**
     * The payment method used for the order.
     */
    private String paymentMethod;

    /**
     * The timestamp indicating when the order was placed.
     */
    private Date orderTime;

    /**
     * The total price of the order.
     */
    private float totalPrice;

    /**
     * Checks if the order has been removed.
     *
     * @return {@code true} if the order has been removed, {@code false}
     * otherwise.
     */
    public boolean isRemoved() {
        return (status & 1) == 1;
    }

    /**
     * Checks if the order has been seen.
     *
     * @return {@code true} if the order has been seen, {@code false} otherwise.
     */
    public boolean isSeen() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the order has been responded.
     *
     * @return {@code true} if the order has been responded, {@code false}
     * otherwise.
     */
    public boolean isResponsed() {
        return ((status >> 1) & 1) == 1;
    }
}
