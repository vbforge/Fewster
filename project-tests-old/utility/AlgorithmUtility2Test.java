package com.vladproduction.fewster.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("constraints")
public class AlgorithmUtility2Test {

    @Autowired
    private AlgorithmUtility algorithmUtility;

    @Value("${short.url.length}")
    private int shortUrlLength;


    @Test
    @DisplayName("AlgorithmUtility2Test")
    void test(){
        String url = "http://example.org/page";
        String shortCode = algorithmUtility.generateShortCode(url);
        assertNotNull(shortCode);
        assertEquals(shortUrlLength, shortCode.length(), "Short code should match configured length");

    }


}
