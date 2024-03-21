package com.cowards.onlyarts.repositories.request;

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
public class RequestDTO {

    private String requestId;
    private String customerID;
    private String publisherId;
    private Date requestTime;
    private String description;
    private int status;

    public boolean isSeen() {
        return ((status >> 3) & 1) == 1;
    }

    public boolean isResponse() {
        return ((status >> 2) & 1) == 1;
    }

    public boolean isApproved() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isRemoved() {
        return (status & 1) == 1;
    }
}
