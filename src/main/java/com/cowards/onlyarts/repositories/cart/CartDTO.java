/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code CartDTO} class represents a data transfer object (DTO) for managing items in a user's cart.
 * It contains information about the user ID and the artwork ID.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CartDTO {

    /** The user ID associated with the cart item. */
    private String userId;
    
    /** The artwork ID associated with the cart item. */
    private String artworkId;
}
