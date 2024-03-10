package com.cowards.onlyarts.repositories.report;

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
public class ReportDTO {

    private String reportId;
    private String artworkId;
    private String reporterId;
    private String description;
    private Date reportTime;
    private int status;

    public boolean isSeen() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isResponsed() {
        return (status & 1) == 1;
    }
}
