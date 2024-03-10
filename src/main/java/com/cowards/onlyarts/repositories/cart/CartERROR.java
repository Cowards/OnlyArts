/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.cart;

/**
 *
 * @author Admin
 */
public class CartERROR extends Exception {

    public CartERROR(String message) {
        super(message);
    }

    public CartERROR(String message, Throwable cause) {
        super(message, cause);
    }

}
