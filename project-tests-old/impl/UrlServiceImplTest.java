package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.security.AuthService;
import com.vladproduction.fewster.utility.GlobalUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceImplTest {

    private UrlRepository urlRepository;
    private AuthService authService;
    private GlobalUtility globalUtility;
    private UrlServiceImpl urlService;

    @BeforeEach
    void setup(){
        urlRepository = mock(UrlRepository.class);
        globalUtility = mock(GlobalUtility.class);
        urlService = new UrlServiceImpl(urlRepository, authService, globalUtility);
    }

    @Test
    @DisplayName("create() with valid URL")
    void testCreateValidUrl() {
        String originalUrl = "https://example.com";
        String shortUrl = "abc123";

        when(globalUtility.isValidUrl(originalUrl)).thenReturn(false);
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(globalUtility.generateUniqueShortUrl(originalUrl)).thenReturn(shortUrl);
        when(urlRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UrlDTO result = urlService.create(originalUrl);

        assertEquals(originalUrl, result.getOriginalUrl());
        assertEquals(shortUrl, result.getShortUrl());
    }

    @Test
    @DisplayName("create() with invalid URL should throw exception")
    void testCreateInvalidUrlThrows() {
        String invalidUrl = "ftp://example.com";
        when(globalUtility.isValidUrl(invalidUrl)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> urlService.create(invalidUrl));
    }

    @Test
    @DisplayName("create() with duplicate URL should return existing")
    void testCreateDuplicateUrl() {
        String originalUrl = "https://example.com";
        UrlEntity existing = new UrlEntity();
        existing.setId(1L);
        existing.setOriginalUrl(originalUrl);
        existing.setShortUrl("abc123");

        when(globalUtility.isValidUrl(originalUrl)).thenReturn(false);
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existing));

        UrlDTO dto = urlService.create(originalUrl);

        assertEquals(originalUrl, dto.getOriginalUrl());
        assertEquals("abc123", dto.getShortUrl());
    }

    @Test
    @DisplayName("createAll() with mixed valid and invalid URLs should throw on first invalid")
    void testCreateAllMixedThrows() {
        List<String> urls = List.of("https://valid1.com", "ftp://invalid.com", "https://valid2.com");
        when(globalUtility.isValidUrl("https://valid1.com")).thenReturn(false);
        when(globalUtility.isValidUrl("ftp://invalid.com")).thenReturn(true);
        when(globalUtility.isValidUrl("https://valid2.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> urlService.createAll(urls));
    }

    @Test
    @DisplayName("getById() with valid ID")
    void testGetByIdValid() {
        UrlEntity entity = new UrlEntity();
        entity.setId(10L);
        entity.setOriginalUrl("https://site.com");
        entity.setShortUrl("abc");

        when(urlRepository.findById(10L)).thenReturn(Optional.of(entity));

        UrlDTO dto = urlService.getById(10L);

        assertEquals("https://site.com", dto.getOriginalUrl());
        assertEquals("abc", dto.getShortUrl());
    }

    @Test
    @DisplayName("getById() with invalid ID should throw")
    void testGetByIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> urlService.getById(0L));
    }

    @Test
    @DisplayName("getById() with non-existing ID should throw")
    void testGetByIdNotFound() {
        when(urlRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> urlService.getById(999L));
    }

    @Test
    @DisplayName("getByShortUrl() with valid input")
    void testGetByShortUrlValid() {
        UrlEntity entity = new UrlEntity();
        entity.setOriginalUrl("https://abc.com");
        entity.setShortUrl("abc");

        when(urlRepository.findByShortUrl("abc")).thenReturn(Optional.of(entity));

        UrlDTO dto = urlService.getByShortUrl("abc");

        assertEquals("https://abc.com", dto.getOriginalUrl());
        assertEquals("abc", dto.getShortUrl());
    }

    @Test
    @DisplayName("getByShortUrl() with null should throw")
    void testGetByShortUrlNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> urlService.getByShortUrl(null));
    }

    @Test
    @DisplayName("getByShortUrl() with empty should throw")
    void testGetByShortUrlEmptyThrows(){
        assertThrows(IllegalArgumentException.class, () -> urlService.getByShortUrl(""));
    }

    @Test
    @DisplayName("getByShortUrl() with not found should throw")
    void testGetByShortUrlNotFoundThrows() {
        when(urlRepository.findByShortUrl("xyz")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> urlService.getByShortUrl("xyz"));
    }

    @Test
    @DisplayName("getAll() with data in DB")
    void testGetAllWithData() {
        UrlEntity entity1 = new UrlEntity(1L, "https://a.com", "a1");
        UrlEntity entity2 = new UrlEntity(2L, "https://b.com", "b2");

        when(urlRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<UrlDTO> list = urlService.getAll();

        assertEquals(2, list.size());
        assertEquals("https://a.com", list.get(0).getOriginalUrl());
    }

    @Test
    @DisplayName("getAll() returns empty list when exception thrown")
    void testGetAllExceptionReturnsEmpty() {
        when(urlRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        List<UrlDTO> list = urlService.getAll();
        assertTrue(list.isEmpty());
    }

}