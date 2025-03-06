package com.github.awesome.mysql.slowlog.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DigestHelper {

    private static final String MD5 = "MD5";

    private DigestHelper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String md5(final String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            byte[] hashBytes = md5.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // This should never happen
            return input;
        }
    }

}
