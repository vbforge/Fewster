package com.vladproduction.fewster.performance;

import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.service.ShortAlgorithmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CollisionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @MockBean
    private ShortAlgorithmService shortAlgorithmService;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
    }

    @Test
    void collisionHandling_ShouldRespectMaxAttempts() throws Exception {
        // Step 1: Insert a pre-existing entry that will collide
        UrlEntity preInserted = new UrlEntity();
        preInserted.setOriginalUrl("https://existing.com");
        preInserted.setShortUrl("DUPLICATE");
        urlRepository.save(preInserted);

        // Step 2: Mock generator to always return "DUPLICATE"
        when(shortAlgorithmService.makeShort(anyString()))
                .thenReturn("DUPLICATE");

        // Step 3: Expect Internal Server Error due to collision retries
        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", "https://new.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Unable to generate unique short URL after 5 attempts for URL: https://new.com"));
    }

}
