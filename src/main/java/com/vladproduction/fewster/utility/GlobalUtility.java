package com.vladproduction.fewster.utility;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.ShortUrlGenerationException;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.service.ShortAlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalUtility {

    private static final Logger log = LoggerFactory.getLogger(GlobalUtility.class);

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    @Value("${generate.unique.short.url.maxAttempt}")
    private int maxAttempt;

    private final UrlRepository urlRepository;
    private final ShortAlgorithmService algorithmService;

    public GlobalUtility(UrlRepository urlRepository, ShortAlgorithmService algorithmService) {
        this.urlRepository = urlRepository;
        this.algorithmService = algorithmService;
    }

    /**
     * Helper method to generate globally unique short URL
     * Note: Short URLs must be unique across ALL users for redirection to work properly
     */
    public String generateUniqueShortUrl(String originalUrl) {
        String shortUrl = algorithmService.makeShort(originalUrl);

        int attempts = 0;

        // Short URLs must be globally unique since they're used for public redirection
        while (attempts < maxAttempt && urlRepository.existsByShortUrl(shortUrl)) {
            log.warn("Short URL collision detected: {}, regenerating... (attempt {})", shortUrl, attempts + 1);
            shortUrl = algorithmService.makeShort(originalUrl + "_" + attempts);
            attempts++;
        }

        if (attempts >= maxAttempt) {
            throw new ShortUrlGenerationException("Unable to generate unique short URL after " + maxAttempt + " attempts for URL: " + originalUrl);
        }

        log.info("Generated unique short URL: {} after {} attempts", shortUrl, attempts);
        return shortUrl;
    }

    /**
     * Helper method to check if original URL already exists for a specific user
     * This prevents users from creating duplicate entries for the same original URL
     */
    public boolean originalUrlExistsForUser(String originalUrl, User user) {
        return urlRepository.findByOriginalUrlAndUser(originalUrl, user).isPresent();
    }

    /**
     * Helper method to check if original URL exists for a specific user, excluding a specific URL ID
     * Useful for update operations where we want to allow updating to the same URL
     */
    public boolean originalUrlExistsForUserExcluding(String originalUrl, User user, Long excludeUrlId) {
        return urlRepository.findByOriginalUrlAndUser(originalUrl, user)
                .filter(urlEntity -> !urlEntity.getId().equals(excludeUrlId))
                .isPresent();
    }

    /**
     * Helper method to check if URL is valid
     */
    public boolean isValidUrl(String url) {
        return url != null &&
                (!url.trim().isEmpty()) &&
                (url.startsWith(HTTP) || url.startsWith(HTTPS));
    }

    /**
     * Helper method to normalize URL (remove trailing slashes, etc.)
     * This helps prevent duplicate URLs like "https://example.com" and "https://example.com/"
     */
    public String normalizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }

        String normalized = url.trim();

        // Remove trailing slash unless it's just the protocol + domain
        if (normalized.endsWith("/") && normalized.length() > 8) {
            // Count slashes to ensure we don't remove the slash from "https://example.com/"
            long slashCount = normalized.chars().filter(ch -> ch == '/').count();
            if (slashCount > 2) { // More than just protocol slashes
                normalized = normalized.substring(0, normalized.length() - 1);
            }
        }

        return normalized;
    }

}
