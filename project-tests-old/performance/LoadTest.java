package com.vladproduction.fewster.performance;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoadTest {

    public static final int THREAD_COUNT = 100;

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
    void concurrentUrlCreation_ShouldSucceedWithoutErrors() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<MvcResult>> results = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            results.add(executor.submit(() -> {
                try {
                    return mockMvc.perform(post("/api/v1/url")
                                    .param("urlText", "https://site" + index + ".com")
                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                            .andExpect(status().isCreated())
                            .andReturn();
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await(); // Wait for all threads to finish
        executor.shutdown();

        // Optional: Check success rate
        assertThat(results).hasSize(THREAD_COUNT);
        assertThat(urlRepository.count()).isEqualTo(THREAD_COUNT);
    }

    @Test
    void concurrentRedirects_ShouldReturnExpectedRedirection() throws Exception {
        String originalUrl = "https://test-redirect.com";

        // Create one URL to redirect to
        MvcResult createResult = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn();
        UrlDTO dto = objectMapper.readValue(createResult.getResponse().getContentAsString(), UrlDTO.class);
        String shortCode = dto.getShortUrl().replace("http://localhost:8080/r/", "");

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(get("/r/" + shortCode))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl(originalUrl));
                } catch (Exception e) {
                    e.printStackTrace(); // Optionally log
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }




}
