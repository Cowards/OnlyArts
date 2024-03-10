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
 *
 * @author Admin
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {

    private String orderId;
    private String userId;
    private int status;
    private String paymentMethod;
    private Date orderTime;
    private float totalPrice;

    public boolean isRemoved() {
        return (status & 1) == 1;
    }

    public boolean isSeen() {
        return ((status >> 2) & 1) == 1;
    }

    public boolean isResponsed() {
        return ((status >> 1) & 1) == 1;
    }
}
