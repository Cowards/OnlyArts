package com.cowards.onlyarts.repositories.token;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@code TokenDTO} class represents a data transfer object (DTO) for
 * managing tokens. It contains fields for the user ID, token string, valid
 * date, expired date, and status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TokenDTO {

    /**
     * The ID of the user associated with the token.
     */
    private String userId;

    /**
     * The token string.
     */
    private String tokenString;

    /**
     * The date when the token is valid.
     */
    private Date validDate;

    /**
     * The date when the token expires.
     */
    private Date expiredDate;

    /**
     * The status of the token.
     */
    private int status;

    /**
     * Checks if the token is a login token.
     *
     * @return {@code true} if the token is a login token, {@code false}
     * otherwise.
     */
    public boolean isLoginToken() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the token is a reset password token.
     *
     * @return {@code true} if the token is a reset password token,
     * {@code false} otherwise.
     */
    public boolean isResetPasswordToken() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the token is valid.
     *
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        return (status & 1) == 1;
    }

    /**
     * Checks if the token is expired.
     *
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public boolean isExpired() {
        return new Date(System.currentTimeMillis()).after(expiredDate);
    }
}
