package com.vladproduction.fewster.endtoend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UrlEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
    }

    @Test
    void completeShorteningFlow_SuccessCase() throws Exception {
        // 1. Create a short URL
        String originalUrl = "https://example.com";

        MvcResult createResult = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn();

        String body = createResult.getResponse().getContentAsString();
        UrlDTO createdDto = objectMapper.readValue(body, UrlDTO.class);

        // 2. Redirect using short URL
        String shortCode = createdDto.getShortUrl().replace("http://localhost:8080/r/", "");

        mockMvc.perform(get("/r/" + shortCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(originalUrl));

        // 3. Retrieve details by ID
        mockMvc.perform(get("/api/v1/url/" + createdDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdDto.getId()))
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortUrl").value(createdDto.getShortUrl()));
    }

    @Test
    void batchProcessingFlow_SuccessCase() throws Exception {
        List<String> urls = List.of("https://site1.com", "https://site2.com", "https://site3.com");

        String jsonPayload = objectMapper.writeValueAsString(urls);

        // 1. Create multiple short URLs
        MvcResult batchResult = mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(urls.size()))
                .andReturn();

        String batchBody = batchResult.getResponse().getContentAsString();
        UrlDTO[] createdBatch = objectMapper.readValue(batchBody, UrlDTO[].class);

        // 2. Validate that each short URL redirects correctly
        for (UrlDTO dto : createdBatch) {
            String code = dto.getShortUrl().replace("http://localhost:8080/r/", "");

            mockMvc.perform(get("/r/" + code))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(dto.getOriginalUrl()));
        }

        // 3. Validate total count in DB
        assertThat(urlRepository.count()).isEqualTo(urls.size());

        // 4. Retrieve each by ID and verify
        for (UrlDTO dto : createdBatch) {
            mockMvc.perform(get("/api/v1/url/" + dto.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.originalUrl").value(dto.getOriginalUrl()))
                    .andExpect(jsonPath("$.shortUrl").value(dto.getShortUrl()));
        }
    }

}
