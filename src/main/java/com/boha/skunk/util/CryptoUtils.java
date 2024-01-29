package com.boha.skunk.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CryptoUtils {

    public static String generateAPISignature(Map<String, String> data, String passPhrase) {
        // Arrange the map by key alphabetically for API calls
        Map<String, String> orderedData = new TreeMap<>(data);

        // Create the query string
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : orderedData.entrySet()) {
            queryString.append(entry.getKey())
                    .append('=')
                    .append(urlEncode(entry.getValue()))
                    .append('&');
        }

        // Remove the last '&'
        queryString.setLength(queryString.length() - 1);

        // Append passPhrase if not null
        if (passPhrase != null) {
            queryString.append("&passphrase=").append(urlEncode(passPhrase.trim()));
        }

        // Hash the data and create the signature
        return md5Hash(queryString.toString());
    }

    private static String urlEncode(String value) {
        return value.replace(" ", "+");
    }

    private static String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
