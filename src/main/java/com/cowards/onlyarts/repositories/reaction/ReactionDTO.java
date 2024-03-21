/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code ReactionDTO} class represents a data transfer object (DTO) for
 * managing reactions to artworks. It contains fields for the artwork ID and
 * user ID.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReactionDTO {

    /**
     * The ID of the artwork associated with the reaction.
     */
    private String artworkId;

    /**
     * The ID of the user who reacted to the artwork.
     */
    private String userId;
}
