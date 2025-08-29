package com.vladproduction.fewster.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UrlIntegrationTest {

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
    void createSingleUrl_SuccessCase() throws Exception {
        String originalUrl = "https://www.example.com";

        MvcResult result = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UrlDTO urlDTO = objectMapper.readValue(responseBody, UrlDTO.class);

        // Verify database record created
        Optional<UrlEntity> savedEntity = urlRepository.findById(urlDTO.getId());
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getOriginalUrl()).isEqualTo(originalUrl);
        assertThat(savedEntity.get().getShortUrl()).isEqualTo(urlDTO.getShortUrl());

        // Verify short URL format
        assertThat(urlDTO.getShortUrl()).startsWith("http://localhost:8080/r/");
    }

    @Test
    void createSingleUrl_InvalidUrlFormat() throws Exception {
        String[] invalidUrls = {"not-a-url", "ftp://example.com", "", "just-text"};

        for (String invalidUrl : invalidUrls) {
            mockMvc.perform(post("/api/v1/url")
                            .param("urlText", invalidUrl)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").exists())
                    .andDo(result -> System.out.println("Response body: " + result.getResponse().getContentAsString()));
        }

        // Verify no database records created
        assertThat(urlRepository.count()).isEqualTo(0);
    }

    @Test
    void createSingleUrl_DuplicateUrl() throws Exception {
        String originalUrl = "https://www.duplicate-test.com";

        // Create URL first time
        MvcResult firstResult = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn();

        UrlDTO firstUrlDTO = objectMapper.readValue(
                firstResult.getResponse().getContentAsString(), UrlDTO.class);

        // Create same URL second time
        MvcResult secondResult = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn();

        UrlDTO secondUrlDTO = objectMapper.readValue(
                secondResult.getResponse().getContentAsString(), UrlDTO.class);

        // Verify same short URL returned
        assertThat(firstUrlDTO.getShortUrl()).isEqualTo(secondUrlDTO.getShortUrl());
        assertThat(firstUrlDTO.getId()).isEqualTo(secondUrlDTO.getId());

        // Verify only one database record exists
        assertThat(urlRepository.count()).isEqualTo(1);
    }

    @Test
    void batchCreateUrls_AllValid() throws Exception {
        List<String> validUrls = Arrays.asList(
                "https://example1.com",
                "https://example2.com",
                "https://example3.com"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUrls)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        UrlDTO[] urlDTOs = objectMapper.readValue(
                result.getResponse().getContentAsString(), UrlDTO[].class);

        // Verify all URLs have unique short URLs
        assertThat(urlDTOs).hasSize(3);
        List<String> shortUrls = Arrays.stream(urlDTOs)
                .map(UrlDTO::getShortUrl)
                .toList();
        assertThat(shortUrls).doesNotHaveDuplicates();

        // Verify database records created
        assertThat(urlRepository.count()).isEqualTo(3);

        // Verify response order matches request order
        for (int i = 0; i < validUrls.size(); i++) {
            assertThat(urlDTOs[i].getOriginalUrl()).isEqualTo(validUrls.get(i));
        }
    }

    @Test
    void batchCreateUrls_MixedValidInvalid() throws Exception {
        List<String> mixedUrls = Arrays.asList(
                "https://valid.com",
                "invalid-url",
                "https://another-valid.com"
        );

        mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mixedUrls)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid Url format: invalid-url"));

        // Verify no database records created (transaction rollback)
        assertThat(urlRepository.count()).isEqualTo(0);
    }

    @Test
    void batchCreateUrls_SomeDuplicates() throws Exception {
        // First create one URL
        String duplicateUrl = "https://example.com";
        UrlEntity existingEntity = new UrlEntity(duplicateUrl, "http://localhost:8080/r/abc123");
        urlRepository.save(existingEntity);

        List<String> urlsWithDuplicates = Arrays.asList(
                duplicateUrl,
                "https://new.com",
                duplicateUrl
        );

        MvcResult result = mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlsWithDuplicates)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        UrlDTO[] urlDTOs = objectMapper.readValue(
                result.getResponse().getContentAsString(), UrlDTO[].class);

        // Verify duplicate URLs return same shortUrl
        assertThat(urlDTOs[0].getShortUrl()).isEqualTo(urlDTOs[1].getShortUrl());
        assertThat(urlDTOs[0].getId()).isEqualTo(urlDTOs[1].getId());

        // Verify only unique database records (1 existing + 1 new)
        assertThat(urlRepository.count()).isEqualTo(2);
    }

    @Test
    void batchCreateUrls_EmptyArray() throws Exception {
        List<String> emptyUrls = List.of();

        mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUrls)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify no database records created
        assertThat(urlRepository.count()).isEqualTo(0);
    }

    @Test
    void getUrlById_ExistingRecord() throws Exception {
        // Setup - create URL record
        UrlEntity entity = new UrlEntity("https://existing.com", "http://localhost:8080/r/test123");
        UrlEntity savedEntity = urlRepository.save(entity);

        mockMvc.perform(get("/api/v1/url/{id}", savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntity.getId()))
                .andExpect(jsonPath("$.originalUrl").value("https://existing.com"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/r/test123"));
    }

    @Test
    void getUrlById_NonExistingId() throws Exception {
        Long nonExistingId = 99999L;

        mockMvc.perform(get("/api/v1/url/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Url with id: " + nonExistingId + " not found."));
    }

    @Test
    void getUrlById_InvalidIdFormat() throws Exception {
        Long[] invalidIds = {0L, -1L};

        for (Long invalidId : invalidIds) {
            mockMvc.perform(get("/api/v1/url/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Id should be positive"));
        }
    }

    @Test
    void getAllUrls_EmptyDatabase() throws Exception {
        mockMvc.perform(get("/api/v1/url/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllUrls_PopulatedDatabase() throws Exception {
        // Setup - create multiple URL records
        List<UrlEntity> entities = Arrays.asList(
                new UrlEntity("https://url1.com", "http://localhost:8080/r/code1"),
                new UrlEntity("https://url2.com", "http://localhost:8080/r/code2"),
                new UrlEntity("https://url3.com", "http://localhost:8080/r/code3")
        );
        urlRepository.saveAll(entities);

        MvcResult result = mockMvc.perform(get("/api/v1/url/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        UrlDTO[] urlDTOs = objectMapper.readValue(
                result.getResponse().getContentAsString(), UrlDTO[].class);

        // Verify each UrlDTO has complete data
        for (UrlDTO urlDTO : urlDTOs) {
            assertThat(urlDTO.getId()).isNotNull();
            assertThat(urlDTO.getOriginalUrl()).isNotNull();
            assertThat(urlDTO.getShortUrl()).isNotNull();
        }

        // Verify count matches database
        assertThat(urlDTOs.length).isEqualTo(urlRepository.count());
    }

    @Test
    void getUrlByShortUrl_ExistingShortUrl() throws Exception {
        // Setup - create URL record
        String shortUrl = "http://localhost:8080/r/existing";
        UrlEntity entity = new UrlEntity("https://original.com", shortUrl);
        urlRepository.save(entity);

        mockMvc.perform(get("/api/v1/url/short")
                        .param("shortUrl", shortUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://original.com"))
                .andExpect(jsonPath("$.shortUrl").value(shortUrl));
    }

    @Test
    void getUrlByShortUrl_NonExistingShortUrl() throws Exception {
        String nonExistingShortUrl = "http://localhost:8080/r/nonexistent";

        mockMvc.perform(get("/api/v1/url/short")
                        .param("shortUrl", nonExistingShortUrl))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("URL with short URL: " + nonExistingShortUrl + " not found."));
    }

    @Test
    void getUrlByShortUrl_EmptyParameter() throws Exception {
        mockMvc.perform(get("/api/v1/url/short")
                        .param("shortUrl", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Short URL should not be empty"));
    }

    @Test
    void getUrlByShortUrl_MissingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/url/short"))
                .andExpect(status().isInternalServerError()); //expected: 500; not 400 as isBadRequest();
    }

    @Test
    void redirect_ValidShortCode() throws Exception {
        // Setup - create URL record
        String originalUrl = "https://redirect-test.com";
        String shortUrl = "http://localhost:8080/r/redirect123";
        UrlEntity entity = new UrlEntity(originalUrl, shortUrl);
        urlRepository.save(entity);

        mockMvc.perform(get("/r/redirect123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void redirect_NonExistingShortCode() throws Exception {
        mockMvc.perform(get("/r/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_MalformedShortCode() throws Exception {
        String[] malformedCodes = {"malformed@code", "toolongcode123456789"};

        for (String malformedCode : malformedCodes) {
            mockMvc.perform(get("/r/{shortCode}", malformedCode))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void completeUrlLifecycleTest() throws Exception {
        String originalUrl = "https://lifecycle-test.com";

        // Step 1: Create URL
        MvcResult createResult = mockMvc.perform(post("/api/v1/url")
                        .param("urlText", originalUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn();

        UrlDTO createdUrlDTO = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UrlDTO.class);

        // Step 2: Verify shortUrl format
        assertThat(createdUrlDTO.getShortUrl()).startsWith("http://localhost:8080/r/");

        // Step 3: Retrieve by ID
        mockMvc.perform(get("/api/v1/url/{id}", createdUrlDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortUrl").value(createdUrlDTO.getShortUrl()));

        // Step 4: Extract shortCode from shortUrl
        String shortCode = createdUrlDTO.getShortUrl().replace("http://localhost:8080/r/", "");

        // Step 5: Test redirect
        mockMvc.perform(get("/r/{shortCode}", shortCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", originalUrl));

        // Step 6: Verify lookup by shortUrl
        mockMvc.perform(get("/api/v1/url/short")
                        .param("shortUrl", createdUrlDTO.getShortUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value(originalUrl));
    }

    @Test
    void batchProcessingIntegrationTest() throws Exception {
        List<String> batchUrls = Arrays.asList(
                "https://batch1.com",
                "https://batch2.com",
                "https://batch3.com"
        );

        // Step 1: Batch create URLs
        MvcResult batchResult = mockMvc.perform(post("/api/v1/url/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchUrls)))
                .andExpect(status().isCreated())
                .andReturn();

        UrlDTO[] createdUrls = objectMapper.readValue(
                batchResult.getResponse().getContentAsString(), UrlDTO[].class);

        // Step 2: Test individual retrieval by ID for each
        for (UrlDTO urlDTO : createdUrls) {
            mockMvc.perform(get("/api/v1/url/{id}", urlDTO.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.originalUrl").value(urlDTO.getOriginalUrl()));
        }

        // Step 3: Test retrieval by shortUrl for each
        for (UrlDTO urlDTO : createdUrls) {
            mockMvc.perform(get("/api/v1/url/short")
                            .param("shortUrl", urlDTO.getShortUrl()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.originalUrl").value(urlDTO.getOriginalUrl()));
        }

        // Step 4: Test redirect functionality for each
        for (UrlDTO urlDTO : createdUrls) {
            String shortCode = urlDTO.getShortUrl().replace("http://localhost:8080/r/", "");
            mockMvc.perform(get("/r/{shortCode}", shortCode))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", urlDTO.getOriginalUrl()));
        }

        // Step 5: Verify GET /api/v1/url/all includes all created URLs
        mockMvc.perform(get("/api/v1/url/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void errorHandlingIntegrationTest() throws Exception {
        // Step 1: Test invalid URL creation
        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", "invalid-url")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // Step 2: Test redirect with non-existent short code
        mockMvc.perform(get("/r/nonexistent"))
                .andExpect(status().isNotFound());

        // Step 3: Test retrieval with non-existent ID
        mockMvc.perform(get("/api/v1/url/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify error response format consistency
        // All error responses should have same structure: timestamp, status, error, message
    }



}



















