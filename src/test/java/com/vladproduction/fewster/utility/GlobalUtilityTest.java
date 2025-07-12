package com.vladproduction.fewster.utility;

import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.service.ShortAlgorithmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalUtilityTest {

    private GlobalUtility globalUtility;
    private UrlRepository urlRepository;
    private ShortAlgorithmService algorithmService;

    @BeforeEach
    void setup(){
        urlRepository = mock(UrlRepository.class);
        algorithmService = mock(ShortAlgorithmService.class);
        globalUtility = new GlobalUtility(urlRepository, algorithmService);

        ReflectionTestUtils.setField(globalUtility, "maxAttempt", 5);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://valid.com",
            "https://secure.com",
            "http://example.org/page",
            "https://example.com/path?q=test"
    })
    @DisplayName("isValidUrl() should return false for valid URLs")
    void testIsValidUrlWithValidUrls(String url) {
        assertFalse(globalUtility.isValidUrl(url), "Expected false for valid URL: " + url);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ftp://invalid.com",
            "file://local/path",
            "localhost",
            "",
            "    ",
            "example.com",
            "htp://typo.com"
    })
    @DisplayName("isValidUrl() should return true for invalid URLs")
    void testIsValidUrlWithInvalidUrls(String url) {
        assertTrue(globalUtility.isValidUrl(url), "Expected true for invalid URL: " + url);
    }

    @Test
    @DisplayName("generateUniqueShortUrl() returns non-colliding short URL within limit")
    void testGenerateUniqueShortUrlSuccess() {
        String originalUrl = "https://example.com/page";
        String initialShort = "abc123";
        String secondAttempt = "xyz789";

        when(algorithmService.makeShort(originalUrl)).thenReturn(initialShort);
        when(urlRepository.existsByShortUrl(initialShort)).thenReturn(true); // First one collides
        when(algorithmService.makeShort(originalUrl + "_0")).thenReturn(secondAttempt);
        when(urlRepository.existsByShortUrl(secondAttempt)).thenReturn(false); // No collision now

        String result = globalUtility.generateUniqueShortUrl(originalUrl);
        assertEquals(secondAttempt, result);
        verify(algorithmService, times(2)).makeShort(anyString());
        verify(urlRepository, times(2)).existsByShortUrl(anyString());
    }

    @Test
    @DisplayName("generateUniqueShortUrl() throws exception after max attempts")
    void testGenerateUniqueShortUrlMaxAttemptReached() {
        String originalUrl = "https://example.com/fail";
        when(algorithmService.makeShort(anyString())).thenReturn("clash");
        when(urlRepository.existsByShortUrl("clash")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> globalUtility.generateUniqueShortUrl(originalUrl));

        assertTrue(ex.getMessage().contains("Unable to generate unique short URL"));
        verify(algorithmService, times(6)).makeShort(anyString()); // 1 + 5 attempts
        verify(urlRepository, times(5)).existsByShortUrl(anyString()); // maxAttempt
    }

}