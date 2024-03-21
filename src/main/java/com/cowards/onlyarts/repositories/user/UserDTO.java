package com.cowards.onlyarts.repositories.user;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code UserDTO} class represents a data transfer object (DTO) for
 * managing user information. It contains fields for the user ID, role ID, first
 * name, last name, avatar, phone number, email, address, join date, biography,
 * status, and password.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    /**
     * The ID of the user.
     */
    private String userId;

    /**
     * The ID of the role associated with the user.
     */
    private String roleId;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The avatar of the user.
     */
    private String avatar;

    /**
     * The phone number of the user.
     */
    private String phone;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The address of the user.
     */
    private String address;

    /**
     * The date when the user joined.
     */
    private Date joinDate;

    /**
     * The biography of the user.
     */
    private String bio;

    /**
     * The status of the user.
     */
    private int status;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * Checks if the user is banned.
     *
     * @return {@code true} if the user is banned, {@code false} otherwise.
     */
    public boolean isBanned() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the user has been removed.
     *
     * @return {@code true} if the user has been removed, {@code false}
     * otherwise.
     */
    public boolean isRemoved() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the user is currently online.
     *
     * @return {@code true} if the user is online, {@code false} otherwise.
     */
    public boolean isOnline() {
        return (status & 1) == 1;
    }
}
