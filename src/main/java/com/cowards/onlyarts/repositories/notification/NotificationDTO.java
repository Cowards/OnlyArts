/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.notification;

/**
 *
 * @author dell
 */

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationDTO {
    private String userId;
    private Date  noticeTime;
    private String description;
    private int status;
    
    public boolean isSeen() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isRemoved() {
        return ((status) & 1) == 1;
    }
}
