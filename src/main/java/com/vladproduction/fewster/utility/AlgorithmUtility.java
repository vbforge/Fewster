package com.vladproduction.fewster.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AlgorithmUtility {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmUtility.class);

    @Value("${short.url.length}")
    private int shortUrlLength;

    @Value("${characters.string.literal}")
    private String characters;

    /**
     * hash-based approach
     * */
    public String generateShortCode(String originalUrl) {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(originalUrl.getBytes());
            String base64Hash = Base64.getEncoder().encodeToString(hash);

            //clean up base64 string and take first 6 characters
            String cleanHash = base64Hash.replace("[+/=]", "");
            return cleanHash.substring(0, Math.min(shortUrlLength, cleanHash.length()));

        }catch (NoSuchAlgorithmException e){
            log.error("Error generating hash", e);
            return generateSimpleShortCode(originalUrl);
        }
    }

    /**
     * use hashCode and convert to base62
     * */
    public String generateSimpleShortCode(String originalUrl) {
        int hash = Math.abs(originalUrl.hashCode());
        StringBuilder shortCode = new StringBuilder();
        while (shortCode.length() < shortUrlLength){
            shortCode.append(characters.charAt(hash % characters.length()));
            hash /= characters.length();
        }

        return shortCode.toString();
    }

}
