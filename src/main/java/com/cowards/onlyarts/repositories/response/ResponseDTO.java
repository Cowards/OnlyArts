package com.cowards.onlyarts.repositories.response;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseDTO {
    private String responseId;
    private String requestId;
    private String image;
    private String description;
    private Date responseTime;
    private int status; //(seen/remove)
    public boolean isRemoved() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isSeen() {
        return (status & 1) == 1;
    }
    
}
