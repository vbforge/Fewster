package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.service.ShortAlgorithmService;
import com.vladproduction.fewster.utility.AlgorithmUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShortAlgorithmServiceImplTest {

    private AlgorithmUtility algorithmUtility;
    private ShortAlgorithmServiceImpl shortAlgorithmService;

    @BeforeEach
    void setUp() {
        algorithmUtility = mock(AlgorithmUtility.class);
        shortAlgorithmService = new ShortAlgorithmServiceImpl(algorithmUtility);

        // Simulate property injection
        ReflectionTestUtils.setField(shortAlgorithmService, "baseUrl", "http://localhost:8080/r/");
    }

    @Test
    @DisplayName("makeShort() should generate correct shortened URL")
    void testMakeShortWithValidUrl() {
        String inputUrl = "https://example.com";
        String generatedCode = "abc123";

        when(algorithmUtility.generateShortCode(inputUrl)).thenReturn(generatedCode);

        String result = shortAlgorithmService.makeShort(inputUrl);

        assertEquals("http://localhost:8080/r/abc123", result);
    }

    @Test
    @DisplayName("makeShort() should throw exception on null input")
    void testMakeShortWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> shortAlgorithmService.makeShort(null));
    }

    @Test
    @DisplayName("makeShort() should throw exception on empty input")
    void testMakeShortWithEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> shortAlgorithmService.makeShort(""));
    }

    @Test
    @DisplayName("makeShort() should include base URL prefix")
    void testMakeShortIncludesBaseUrlPrefix() {
        String input = "https://my.site";
        String code = "xyz987";
        when(algorithmUtility.generateShortCode(input)).thenReturn(code);

        String result = shortAlgorithmService.makeShort(input);
        assertTrue(result.startsWith("http://localhost:8080/r/"));
        assertTrue(result.endsWith(code));
    }

}