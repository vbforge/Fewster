package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.mapper.UrlMapper;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.security.AuthService;
import com.vladproduction.fewster.service.UrlService;
import com.vladproduction.fewster.utility.GlobalUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final AuthService authService;
    private final GlobalUtility globalUtility;

    public UrlServiceImpl(UrlRepository urlRepository, AuthService authService, GlobalUtility globalUtility) {
        this.urlRepository = urlRepository;
        this.authService = authService;
        this.globalUtility = globalUtility;
    }

    @Override
    public UrlDTO create(String urlText, boolean isDemo) {
        log.info("Attempting to create and save url: {}", urlText);


        // Get current authenticated user
        User currentUser = isDemo ? authService.getDemoUser() : authService.getCurrentUser();

        // Normalize the URL to prevent duplicates like "https://example.com" and "https://example.com/"
        String normalizedUrl = globalUtility.normalizeUrl(urlText);

        // Validate URL format
        if (globalUtility.isValidUrl(normalizedUrl)) {
            throw new IllegalArgumentException("Invalid Url format: " + normalizedUrl);
        }

        // Check for duplicates for this specific user using the utility method
        if (globalUtility.originalUrlExistsForUser(normalizedUrl, currentUser)) {
            log.info("URL already exists for user {}, returning existing short URL", currentUser.getUsername());
            UrlEntity existingUrl = urlRepository.findByOriginalUrlAndUser(normalizedUrl, currentUser).get();
            return UrlMapper.toDTO(existingUrl);
        }

        String shortUrl = globalUtility.generateUniqueShortUrl(normalizedUrl);

        log.info("Creating URL Entity for user: {} where original Url: {} and short Url: {}",
                currentUser.getUsername(), normalizedUrl, shortUrl);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(normalizedUrl);
        urlEntity.setShortUrl(shortUrl);
        urlEntity.setUser(currentUser); // Associate with current user

        log.info("Attempting to save url: {} ", urlEntity);
        UrlEntity entity = urlRepository.save(urlEntity);
        log.info("Saved successfully to database");

        return UrlMapper.toDTO(entity);
    }

    @Override
    public List<UrlDTO> getAllUrlsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        log.info("Fetching all URLs for user: {}", currentUser.getUsername());

        List<UrlEntity> userUrls = urlRepository.findByUser(currentUser);
        return userUrls.stream()
                .map(UrlMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UrlDTO getUrlById(Long id) {
        User currentUser = authService.getCurrentUser();

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("URL not found or access denied for ID: " + id));

        return UrlMapper.toDTO(urlEntity);
    }

    @Override
    public UrlDTO updateUrl(Long id, String newOriginalUrl) {
        User currentUser = authService.getCurrentUser();

        // Normalize the URL
        String normalizedUrl = globalUtility.normalizeUrl(newOriginalUrl);

        // Validate URL format
        if (globalUtility.isValidUrl(normalizedUrl)) {
            throw new IllegalArgumentException("Invalid Url format: " + normalizedUrl);
        }

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("URL not found or access denied for ID: " + id));

        // Check if the new URL already exists for this user (excluding current URL)
        if (globalUtility.originalUrlExistsForUserExcluding(normalizedUrl, currentUser, id)) {
            throw new IllegalArgumentException("This URL already exists in your account");
        }

        urlEntity.setOriginalUrl(normalizedUrl);
        // Optionally regenerate short URL or keep the same one
        // urlEntity.setShortUrl(globalUtility.generateUniqueShortUrl(normalizedUrl));

        UrlEntity updatedEntity = urlRepository.save(urlEntity);
        log.info("Updated URL with ID: {} for user: {}", id, currentUser.getUsername());

        return UrlMapper.toDTO(updatedEntity);
    }

    @Override
    public void deleteUrl(Long id) {
        User currentUser = authService.getCurrentUser();

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("URL not found or access denied for ID: " + id));

        urlRepository.delete(urlEntity);
        log.info("Deleted URL with ID: {} for user: {}", id, currentUser.getUsername());
    }

    // This method can be used for public redirection (no authentication required)
    @Override
    public String getOriginalByShortUrl(String shortUrl) {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Original URL not found by short: " + shortUrl));

        // Increment click count
        urlEntity.incrementClickCount();
        urlRepository.save(urlEntity);

        log.info("Redirecting short URL: {} to: {} (click count: {})",
                shortUrl, urlEntity.getOriginalUrl(), urlEntity.getClickCount());

        return urlEntity.getOriginalUrl();
    }

}
