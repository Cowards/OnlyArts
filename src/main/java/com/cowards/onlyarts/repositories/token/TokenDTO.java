package com.cowards.onlyarts.repositories.token;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for representing a token.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TokenDTO {

    private String userId;
    private String tokenString;
    private Date validDate;
    private Date expiredDate;
    private int status;

    /**
     * Checks if the token is a login token.
     *
     * @return true if the token is a login token, false otherwise.
     */
    public boolean isLoginToken() {
        return ((status >> 2) & 1) == 1;
    }

    /**
     * Checks if the token is a reset password token.
     *
     * @return true if the token is a reset password token, false otherwise.
     */
    public boolean isResetPasswordToken() {
        return ((status >> 1) & 1) == 1;
    }

    /**
     * Checks if the token is valid.
     *
     * @return true if the token is valid, false otherwise.
     */
    public boolean isValid() {
        return ((status) & 1) == 1;
    }

    /**
     * Checks if the token is expired.
     *
     * @return true if the token is expired, false otherwise.
     */
    public boolean isExpired() {
        return new Date(System.currentTimeMillis()).after(expiredDate);
    }
}
