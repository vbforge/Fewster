package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/r")
public class UrlRedirectRestController {

    private static final Logger log = LoggerFactory.getLogger(UrlRedirectRestController.class);

    private final UrlService urlService;

    @Value("${base.url.prefix}")
    private String baseUrl;

    public UrlRedirectRestController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**[GET] redirect to original URL using short code
    ResponseEntity with status code 200 SUCCESS*/
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {

        log.info("Received request to redirect for short code: {}", shortCode);
        UrlDTO urlDTO = urlService.getByShortUrl(baseUrl + shortCode);
        response.sendRedirect(urlDTO.getOriginalUrl());

//        try{
//            log.info("Received request to redirect for short code: {}", shortCode);
//            UrlDTO urlDTO = urlService.getByShortUrl(baseUrl + shortCode);
//            response.sendRedirect(urlDTO.getOriginalUrl());
//        }catch (RuntimeException e){
//            log.error("Short URL not found: {}", shortCode);
//            try {
//                response.sendError(HttpStatus.NOT_FOUND.value(), "Short URL not found");
//            } catch (IOException ioException) {
//                log.error("Error sending error response", ioException);
//            }
//        }catch (Exception e){
//            log.error("Error redirecting", e);
//            try {
//                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
//            } catch (IOException ioException) {
//                log.error("Error sending error response", ioException);
//            }
//        }
    }


}
