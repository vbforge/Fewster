package com.vladproduction.fewster.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmUtilityTest {

    private AlgorithmUtility algorithmUtility;

    @BeforeEach
    void setup(){
        algorithmUtility = new AlgorithmUtility();
        ReflectionTestUtils.setField(algorithmUtility, "shortUrlLength", 6);
        ReflectionTestUtils.setField(algorithmUtility, "characters", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    //Test with different types of valid URLs
    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com",
            "http://example.org/page",
            "https://subdomain.example.co.uk/path/to/resource",
            "https://example.com/?q=chatgpt",
            "https://example.com/#anchor"
    })
    @DisplayName("Test generateShortCode() with various valid URLs")
    void testGenerateShortCodeWithVariousUrls(String url) {
        String shortCode = algorithmUtility.generateShortCode(url);
        assertNotNull(shortCode);
        assertEquals(6, shortCode.length(), "Short code should match configured length");
    }

    //Ensure fallback still works and respects length
    @Test
    @DisplayName("Test generateSimpleShortCode() fallback")
    void testGenerateSimpleShortCodeFallback() {
        String url = "https://fallback.example.com";
        String fallbackCode = algorithmUtility.generateSimpleShortCode(url);
        assertNotNull(fallbackCode);
        assertEquals(6, fallbackCode.length());
    }

    //Hashing is deterministic
    @Test
    @DisplayName("Test consistent output for same input")
    void testConsistentOutputForSameInput() {
        String url = "https://consistent.com/page";
        String code1 = algorithmUtility.generateShortCode(url);
        String code2 = algorithmUtility.generateShortCode(url);
        assertEquals(code1, code2, "Same URL should produce consistent short code");
    }

    //Robustness for non-ASCII, symbols, whitespace
    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com/üñîçødê",
            "https://example.com/<>?{}[]",
            "https://example.com/space here",
            "https://example.com/😊",
            "https://example.com/~!@#$%^&*()"
    })
    @DisplayName("Test handling of special characters and edge cases")
    void testSpecialCharactersHandling(String url) {
        String shortCode = algorithmUtility.generateShortCode(url);
        assertNotNull(shortCode);
        assertEquals(6, shortCode.length());
    }

    //Short codes don’t collide easily for different URLs
    @Test
    @DisplayName("Test uniqueness of short codes for different URLs")
    void testUniquenessForDifferentUrls() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String url = "https://example.com/page/" + i;
            String code = algorithmUtility.generateShortCode(url);
            assertFalse(generated.contains(code), "Short code should be unique among test set");
            generated.add(code);
        }
    }

}