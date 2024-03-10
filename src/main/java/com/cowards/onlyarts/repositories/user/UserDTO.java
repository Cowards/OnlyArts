package com.cowards.onlyarts.repositories.user;

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
public class UserDTO {

    private String userId;
    private String roleId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String phone;
    private String email;
    private String address;
    private Date joinDate;
    private String bio;
    private int status;
    private String password;

    public boolean isBanned() {
        return ((status >> 2) & 1) == 1;
    }

    public boolean isRemoved() {
        return ((status >> 1) & 1) == 1;
    }

    public boolean isOnline() {
        return ((status) & 1) == 1;
    }
}
