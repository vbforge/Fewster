package com.vladproduction.fewster.controller.rest;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/url")
public class UrlRestController {

    private final UrlService urlService;

    public UrlRestController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Create a new short URL for the current user
     * POST: <a href="http://localhost:8080/api/v1/url?urlText={provide potential url for create and save}">...</a>
     */
    @PostMapping
    public ResponseEntity<UrlDTO> create(@RequestParam String urlText) {
        log.info("Received request to create short URL for: {}", urlText);
        UrlDTO createdUrl = urlService.create(urlText, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUrl);
    }

    /**
     * Get all URLs for the current authenticated user
     * GET: <a href="http://localhost:8080/api/v1/url/my-urls">...</a>
     */
    @GetMapping("/my-urls")
    public ResponseEntity<List<UrlDTO>> getMyUrls() {
        log.info("Received request to get all URLs for current user");
        List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
        return ResponseEntity.ok(userUrls);
    }

    /**
     * Get a specific URL by ID (only if it belongs to current user)
     * GET: <a href="http://localhost:8080/api/v1/url/{urlID}">...</a>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UrlDTO> getUrlById(@PathVariable Long id) {
        log.info("Received request to get URL with ID: {}", id);
        UrlDTO urlDTO = urlService.getUrlById(id);
        return ResponseEntity.ok(urlDTO);
    }

    /**
     * Update an existing URL (only if it belongs to current user)
     * PUT: <a href="http://localhost:8080/api/v1/url/{urlID}?newOriginalUrl={new original url}">...</a>
     */
    @PutMapping("/{id}")
    public ResponseEntity<UrlDTO> updateUrl(@PathVariable Long id, @RequestParam String newOriginalUrl) {
        log.info("Received request to update URL with ID: {} to new URL: {}", id, newOriginalUrl);
        UrlDTO updatedUrl = urlService.updateUrl(id, newOriginalUrl);
        return ResponseEntity.ok(updatedUrl);
    }

    /**
     * Delete a URL (only if it belongs to current user)
     * DELETE: <a href="http://localhost:8080/api/v1/url/{urlID}">...</a>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        log.info("Received request to delete URL with ID: {}", id);
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get URL by short URL passing in request parameter (ResponseEntity with status code 200 SUCCESS)
     * GET: <a href="http://localhost:8080/api/v1/url/shortUrl?shortUrl={provide short ulr as parameter for this request}">...</a>
     */
    @GetMapping("/shortUrl")
    public ResponseEntity<String> getOriginalByShortUrl(@RequestParam String shortUrl){

        log.info("Received request to get URL by short URL: {}", shortUrl);
        String originalUrl = urlService.getOriginalByShortUrl(shortUrl);
        return ResponseEntity.ok(originalUrl);

    }

}

