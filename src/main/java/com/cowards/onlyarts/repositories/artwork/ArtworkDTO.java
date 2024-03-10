package com.cowards.onlyarts.repositories.artwork;

import java.io.Serializable;
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
public class ArtworkDTO implements Serializable {

    private String artworkId;
    private String ownerId;
    private String cateId;
    private String name;
    private String description;
    private String artworkImage;
    private float price;
    private Date releasedDate;
    private int status;

    public boolean isPrivate() {
        return ((status >> 2) & 1) == 1; //0b000 ^ 0b100
    }

    public boolean isBanned() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isRemoved() {
        return ((status) & 1) == 1;
    }
}
