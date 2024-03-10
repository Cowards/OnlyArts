package com.cowards.onlyarts.core;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.UUID;

public final class CodeGenerator {

    public static String generateRandomToken(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

   public static String generateUUID(int length) {
        UUID uuid = UUID.randomUUID();
        Date date = new Date(System.currentTimeMillis());
        String _date = date.toString();
        String uuidStr = (_date + uuid.toString())
                .replaceAll("-", "")
                .replaceAll("\\D", "")
                .substring(0, Math.min(length, 32)); // Ensure the length does not exceed UUID length
        return uuidStr;
    }
}
