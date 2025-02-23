package com.github.awesome.mysql.slowlog.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class DigestHelper {

    private static final String MD5 = "MD5";

    private DigestHelper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String md5Base64Hash(final String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            byte[] hashBytes = md5.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // This should never happen
            return input;
        }
    }

}
