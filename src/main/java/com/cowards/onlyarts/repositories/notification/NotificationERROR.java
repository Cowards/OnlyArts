/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.notification;

/**
 *
 * @author dell
 */
public class NotificationERROR extends Exception {

    public NotificationERROR(String message) {
        super(message);
    }

    public NotificationERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
