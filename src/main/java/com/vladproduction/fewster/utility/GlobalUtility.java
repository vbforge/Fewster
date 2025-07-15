package com.vladproduction.fewster.utility;

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
     * helper method to generate unique short URL
     */
    public String generateUniqueShortUrl(String originalUrl) {
        String shortUrl = algorithmService.makeShort(originalUrl);

        int attempts = 0;

        while (attempts < maxAttempt && urlRepository.existsByShortUrl(shortUrl)) {
            log.warn("Short URL collision detected: {}, regenerating...", shortUrl);
            shortUrl = algorithmService.makeShort(originalUrl + "_" + attempts);
            attempts++;
        }

        if (attempts >= maxAttempt) {
            throw new ShortUrlGenerationException("Unable to generate unique short URL after " + maxAttempt + " attempts for URL: " + originalUrl);
        }

        return shortUrl;
    }

    /**
     * helper method to check if url is valid
     */
    public boolean isValidUrl(String url) {
        return url == null ||
                (url.trim().isEmpty()) ||
                (!url.startsWith(HTTP) && !url.startsWith(HTTPS));

    }

}
