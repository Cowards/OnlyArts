/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.response;

/**
 *
 * @author truon
 */
public class ResponseERROR extends Exception{
    
    public ResponseERROR(String message) {
        super(message);
    }
    public ResponseERROR(String message, Throwable cause) {
        super(message, cause);
    }
    
}
