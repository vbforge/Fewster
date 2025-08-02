package com.vladproduction.fewster.controller.rest;

import com.vladproduction.fewster.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/r")
public class UrlRedirectRestController {

    @Value("${base.url.prefix}")
    private String baseUrl;

    private final UrlService urlService;

    public UrlRedirectRestController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Get redirect to original URL using short code (ResponseEntity with status code 200 SUCCESS)
     * GET: <a href="http://localhost:8080/r/{shortCode}">...</a>
     */
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        log.info("Received request to redirect for short code: {}", shortCode);
        String originalUrl = urlService.getOriginalByShortUrl(baseUrl + shortCode);
        log.info("Redirecting to original URL: {} from this short code: {}", originalUrl, shortCode);
        response.sendRedirect(originalUrl);
    }

}
