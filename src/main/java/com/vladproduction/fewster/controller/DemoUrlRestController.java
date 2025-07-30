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
@RequestMapping("/api/v1/demo-url")
public class DemoUrlRestController {

    private static final Logger log = LoggerFactory.getLogger(DemoUrlRestController.class);

    private final UrlService urlService;

    public DemoUrlRestController(UrlService urlService) {
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
        UrlDTO createdUrl = urlService.create(urlText, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUrl);
    }



}

