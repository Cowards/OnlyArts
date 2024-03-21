/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.request;

/**
 *
 * @author truon
 */
public class RequestERROR extends Exception{
    public RequestERROR(String message) {
        super(message);
    }
    public RequestERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
