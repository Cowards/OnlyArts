/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.notification;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code NotificationDTO} class represents a data transfer object (DTO) for
 * managing notifications. It contains fields for the user ID, notification
 * time, description, and status.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationDTO {

    /**
     * The ID of the user associated with the notification.
     */
    private String userId;

    /**
     * The timestamp indicating when the notification occurred.
     */
    private Date noticeTime;

    /**
     * The description or content of the notification.
     */
    private String description;

    /**
     * The status of the notification.
     */
    private int status;

    /**
     * Checks if the notification has been seen.
     *
     * @return {@code true} if the notification has been seen, {@code false}
     * otherwise.
     */
    public boolean isSeen() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the notification has been removed.
     *
     * @return {@code true} if the notification has been removed, {@code false}
     * otherwise.
     */
    public boolean isRemoved() {
        return (status & 1) == 1;
    }
}
