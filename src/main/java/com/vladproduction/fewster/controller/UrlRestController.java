package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/url")
public class UrlRestController {

    private static final Logger log = LoggerFactory.getLogger(UrlRestController.class);

    private final UrlService urlService;

    public UrlRestController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Create a new short URL for the current user
     * POST /api/v1/url
     * <a href="http://localhost:8080/api/v1/url?urlText={provide potential url for create and save}">...</a>
     */
    @PostMapping
    public ResponseEntity<UrlDTO> create(@RequestParam String urlText) {
        log.info("Received request to create short URL for: {}", urlText);
        UrlDTO createdUrl = urlService.create(urlText);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUrl);
    }

    /**
     * Get all URLs for the current authenticated user
     * GET /api/v1/url/my-urls
     * <a href="http://localhost:8080/api/v1/url/my-urls">...</a>
     */
    @GetMapping("/my-urls")
    public ResponseEntity<List<UrlDTO>> getMyUrls() {
        log.info("Received request to get all URLs for current user");
        List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
        return ResponseEntity.ok(userUrls);
    }

    /**
     * Get a specific URL by ID (only if it belongs to current user)
     * GET /api/v1/url/{id}
     * <a href="http://localhost:8080/api/v1/url/{urlID}">...</a>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UrlDTO> getUrlById(@PathVariable Long id) {
        log.info("Received request to get URL with ID: {}", id);
        UrlDTO urlDTO = urlService.getUrlById(id);
        return ResponseEntity.ok(urlDTO);
    }

    /**
     * Update an existing URL (only if it belongs to current user)
     * PUT /api/v1/url/{id}
     * <a href="http://localhost:8080/api/v1/url/{urlID}?newOriginalUrl={new original url}">...</a>
     */
    @PutMapping("/{id}")
    public ResponseEntity<UrlDTO> updateUrl(@PathVariable Long id, @RequestParam String newOriginalUrl) {
        log.info("Received request to update URL with ID: {} to new URL: {}", id, newOriginalUrl);
        UrlDTO updatedUrl = urlService.updateUrl(id, newOriginalUrl);
        return ResponseEntity.ok(updatedUrl);
    }

    /**
     * Delete a URL (only if it belongs to current user)
     * DELETE /api/v1/url/{id}
     * <a href="http://localhost:8080/api/v1/url/{urlID}">...</a>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        log.info("Received request to delete URL with ID: {}", id);
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }

    /**[GET] Get URL by short URL passing in request param
     ResponseEntity with status code 200 SUCCESS*/
    @GetMapping("/shortUrl")
    public ResponseEntity<String> getOriginalByShortUrl(@RequestParam String shortUrl){

        log.info("Received request to get URL by short URL: {}", shortUrl);
        String originalUrl = urlService.getOriginalByShortUrl(shortUrl);
        return ResponseEntity.ok(originalUrl);

    }

}

