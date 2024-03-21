/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cowards.onlyarts.repositories.comment;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code CommentDTO} class represents a data transfer object (DTO) for
 * managing comments on artworks. It contains fields for the comment ID, artwork
 * ID, commenter ID, description, and comment time.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO {

    /**
     * The ID of the comment.
     */
    private String commentId;

    /**
     * The ID of the artwork the comment is associated with.
     */
    private String artworkId;

    /**
     * The ID of the commenter.
     */
    private String commenterId;

    /**
     * The description or content of the comment.
     */
    private String description;

    /**
     * The timestamp indicating when the comment was made.
     */
    private Date comment_time;
}
