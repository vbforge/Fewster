package com.vladproduction.fewster.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public static final String TIMESTAMP = "timestamp";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex){
        log.error("Illegal argument exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(TIMESTAMP, LocalDateTime.now());
        errorResponse.put(STATUS, HttpStatus.BAD_REQUEST.value());
        errorResponse.put(ERROR, "Bad Request");
        errorResponse.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(TIMESTAMP, LocalDateTime.now());
        errorResponse.put(STATUS, HttpStatus.NOT_FOUND.value());
        errorResponse.put(ERROR, "Not Found");
        errorResponse.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ShortUrlGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleShortUrlGenerationException(ShortUrlGenerationException ex) {
        log.error("Short URL generation error: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(TIMESTAMP, LocalDateTime.now());
        errorResponse.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put(ERROR, "Internal Server Error");
        errorResponse.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(TIMESTAMP, LocalDateTime.now());
        errorResponse.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put(ERROR, "Internal Server Error");
        errorResponse.put(MESSAGE, "An unexpected error occurred");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
