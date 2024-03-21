package com.cowards.onlyarts.core;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.UUID;

/**
 * The {@code CodeGenerator} class provides utility methods for generating
 * random tokens and UUIDs. These methods can be used to generate secure and
 * unique identifiers for various purposes.
 */
public final class CodeGenerator {

    /**
     * Generates a random token of the specified length using characters from
     * the set of uppercase letters, lowercase letters, and digits.
     *
     * @param length The length of the token to be generated.
     * @return A randomly generated token.
     */
    public static String generateRandomToken(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a UUID (Universally Unique Identifier) of the specified length
     * by combining the current date and time with a random UUID.
     *
     * @param length The length of the UUID to be generated.
     * @return A UUID generated from the current date, time, and a random UUID.
     */
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
