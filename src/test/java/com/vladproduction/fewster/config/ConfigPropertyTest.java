package com.vladproduction.fewster.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"short.url.length=9", "base.url.prefix=https://custom.short/", "generate.unique.short.url.maxAttempt=3"})
@ActiveProfiles("constraints")
public class ConfigPropertyTest {

    @Autowired
    private Environment env;

    @Test
    void shouldOverrideShortUrlLength() {
        assertThat(env.getProperty("short.url.length")).isEqualTo("9");
    }

    @Test
    void shouldUseCustomPrefix() {
        assertThat(env.getProperty("base.url.prefix")).isEqualTo("https://custom.short/");
    }

    @Test
    void shouldUseCustomMaxAttempts() {
        assertThat(env.getProperty("generate.unique.short.url.maxAttempt")).isEqualTo("3");
    }

}
