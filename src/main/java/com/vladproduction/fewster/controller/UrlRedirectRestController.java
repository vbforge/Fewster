package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/r")
public class UrlRedirectRestController {

    private static final Logger log = LoggerFactory.getLogger(UrlRedirectRestController.class);

    @Value("${base.url.prefix}")
    private String baseUrl;

    private final UrlService urlService;

    public UrlRedirectRestController(UrlService urlService) {
        this.urlService = urlService;
    }

//    /**
//     * Public endpoint to redirect short URLs to original URLs
//     * GET /r/{shortCode}
//     */
//    @GetMapping("/{shortCode}")
//    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
//        try {
//            log.info("Received redirect request for short code: {}", shortCode);
//            String originalUrl = urlService.getOriginalUrl(shortCode);
//
//            log.info("Redirecting {} to {}", shortCode, originalUrl);
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(URI.create(originalUrl))
//                    .build();
//        } catch (Exception e) {
//            log.error("Error redirecting short code {}: {}", shortCode, e.getMessage());
//            return ResponseEntity.notFound().build();
//        }
//    }


    /**
     * [GET] redirect to original URL using short code
     * ResponseEntity with status code 200 SUCCESS
     */
    @GetMapping("/{shortCode}")
    public void redirect2(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        log.info("Received request to redirect for short code: {}", shortCode);
        String originalUrl = urlService.getOriginalByShortUrl(baseUrl + shortCode);
        response.sendRedirect(originalUrl);
    }

}
