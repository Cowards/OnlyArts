package com.cowards.onlyarts.repositories.artwork;

import java.io.Serializable;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code ArtworkDTO} class represents a data transfer object (DTO) for
 * artwork information. It contains fields such as artwork ID, owner ID,
 * category ID, name, description, image URL, price, release date, and status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ArtworkDTO implements Serializable {

    /**
     * The unique identifier for the artwork.
     */
    private String artworkId;

    /**
     * The ID of the owner of the artwork.
     */
    private String ownerId;

    /**
     * The ID of the category the artwork belongs to.
     */
    private String cateId;

    /**
     * The name of the artwork.
     */
    private String name;

    /**
     * The description of the artwork.
     */
    private String description;

    /**
     * The URL of the artwork image.
     */
    private String artworkImage;

    /**
     * The price of the artwork.
     */
    private float price;

    /**
     * The release date of the artwork.
     */
    private Date releasedDate;

    /**
     * The status of the artwork.
     */
    private int status;

    /**
     * Checks if the artwork is marked as private.
     *
     * @return {@code true} if the artwork is private, {@code false} otherwise.
     */
    public boolean isPrivate() {
        return ((status >> 2) & 1) == 1; //0b000 ^ 0b100
    }

    /**
     * Checks if the artwork is banned.
     *
     * @return {@code true} if the artwork is banned, {@code false} otherwise.
     */
    public boolean isBanned() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the artwork is removed.
     *
     * @return {@code true} if the artwork is removed, {@code false} otherwise.
     */
    public boolean isRemoved() {
        return ((status) & 1) == 1;
    }
}
