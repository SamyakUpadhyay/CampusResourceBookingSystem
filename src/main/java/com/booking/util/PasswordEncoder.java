package com.booking.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility class for encoding and verifying user passwords.
 */
public class PasswordEncoder {

    private static final String SALT = "CRSBS";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PasswordEncoder() {
        // Utility class — no instances permitted.
    }

    /**
     * Encodes a plain-text password into a stored hash value.
     *
     * @param plainPassword the raw password to encode
     * @return the encoded password hash
     */
    public static String encode(String plainPassword) {
        Objects.requireNonNull(plainPassword, "plainPassword must not be null");
        String saltedPassword = SALT + plainPassword;
        return Base64.getEncoder().encodeToString(saltedPassword.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifies a plain-text password against a stored hash.
     *
     * @param plainPassword the raw password to verify
     * @param storedHash    the previously encoded hash
     * @return {@code true} if the password matches the hash
     */
    public static boolean matches(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        return encode(plainPassword).equals(storedHash);
    }
}
