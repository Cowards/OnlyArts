/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.orderdetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code OrderDetailDTO} class represents a data transfer object (DTO) for
 * managing order details. It contains fields for the order ID and artwork ID.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailDTO {

    /**
     * The ID of the order.
     */
    private String orderId;

    /**
     * The ID of the artwork associated with the order.
     */
    private String artworkId;
}
