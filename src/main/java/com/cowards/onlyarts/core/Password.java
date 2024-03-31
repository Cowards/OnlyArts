package com.cowards.onlyarts.core;

import org.mindrot.jbcrypt.BCrypt;

public final class Password {

    public static String hashPw(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPw(String plainTextPw, String pw) {
        return BCrypt.checkpw(plainTextPw, pw);
    }
}
