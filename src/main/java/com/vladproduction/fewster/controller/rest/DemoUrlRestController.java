package com.vladproduction.fewster.controller.rest;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/demo-url")
public class DemoUrlRestController {

    private final UrlService urlService;

    public DemoUrlRestController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Create a new short URL for the demo user
     * POST /api/v1/demo-url
     * <a href="http://localhost:8080/api/v1/demo-url?urlText={provide potential url for create}">...</a>
     */
    @PostMapping
    public ResponseEntity<UrlDTO> create(@RequestParam String urlText) {
        log.info("Received request to create short URL for: {}", urlText);
        UrlDTO createdUrl = urlService.create(urlText, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUrl);
    }

}

