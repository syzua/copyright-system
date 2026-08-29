package com.syzua.copyright.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtils {

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }

    public static String generateBlockHash(String contentHash, String authorName, long timestamp) {
        return sha256(contentHash + authorName + timestamp + "copyright-blockchain-salt");
    }

    public static String generateRegNo(long timestamp, long id) {
        return "CR-" + timestamp + "-" + String.format("%04d", id);
    }
}
