/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.order;

/**
 *
 * @author ngocn
 */
public class OrderERROR extends Exception {

    public OrderERROR(String message) {
        super(message);
    }

    public OrderERROR(String message, Throwable cause) {
        super(message, cause);
    }

}
