package com.vladproduction.fewster.mapper;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlMapperTest {

    @Test
    @DisplayName("toDTO() should return null when input is null")
    void testToDTONullInput() {
        UrlDTO dto = UrlMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    @DisplayName("toEntity() should return null when input is null")
    void testToEntityNullInput() {
        UrlEntity entity = UrlMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    @DisplayName("toDTO() should correctly map fields from UrlEntity")
    void testToDTOWithValidEntity() {
        UrlEntity entity = new UrlEntity();
        entity.setId(42L);
        entity.setOriginalUrl("https://example.com");
        entity.setShortUrl("abc123");

        UrlDTO dto = UrlMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(42L, dto.getId());
        assertEquals("https://example.com", dto.getOriginalUrl());
        assertEquals("abc123", dto.getShortUrl());
    }

    @Test
    @DisplayName("toEntity() should correctly map fields from UrlDTO")
    void testToEntityWithValidDTO() {
        UrlDTO dto = new UrlDTO();
        dto.setId(101L);
        dto.setOriginalUrl("https://test.com");
        dto.setShortUrl("xyz789");

        UrlEntity entity = UrlMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(101L, entity.getId());
        assertEquals("https://test.com", entity.getOriginalUrl());
        assertEquals("xyz789", entity.getShortUrl());
    }


}