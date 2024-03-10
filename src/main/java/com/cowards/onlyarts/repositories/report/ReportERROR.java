/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.report;

/**
 *
 * @author dell
 */
public class ReportERROR extends Exception {

    /**
     * Constructs a new instance of ReportERROR with the specified detail
     * message.
     *
     * @param message the detail message.
     */
    public ReportERROR(String message) {
        super(message);
    }

    /**
     * Constructs a new instance of ReportERROR with the specified detail
     * message and cause.
     *
     * @param message the detail message.
     * @param cause the cause (which is saved for later retrieval by the
     * Throwable.getCause() method).
     */
    public ReportERROR(String message, Throwable cause) {
        super(message, cause);
    }

}
