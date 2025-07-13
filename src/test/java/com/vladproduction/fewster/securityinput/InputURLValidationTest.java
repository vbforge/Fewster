package com.vladproduction.fewster.securityinput;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InputURLValidationTest {

    @Autowired
    private MockMvc mockMvc;


    //todo: need to fix logic against sql injection for url as param
    //@Test
    void sqlInjectionAttempt_ShouldBeRejected() throws Exception {
        String sqlInjection = "https://example.com/' OR '1'='1";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", sqlInjection)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void xssInjectionAttempt_ShouldBeRejected() throws Exception {
        String xssPayload = "<script>alert('xss')</script>";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", xssPayload)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void maliciousUrlAttempt_ShouldBeRejected() throws Exception {
        String maliciousUrl = "javascript:alert('evil')";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", maliciousUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void unsupportedProtocol_ShouldBeRejected() throws Exception {
        String unsupportedUrl = "ftp://example.com";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", unsupportedUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    //todo: should response correct status (Status expected:<400> but was:<404>)
    //@Test
    void extremelyLongUrl_ShouldBeRejected() throws Exception {
        String longUrl = "https://example.com/" + "a".repeat(2100);

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", longUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void urlWithSpecialCharacters_ShouldPass() throws Exception {
        String validEncoded = "https://example.com/search?q=hello%20world";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", validEncoded)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists());
    }

    @Test
    void internationalDomainName_ShouldPassIfValid() throws Exception {
        String idnUrl = "https://примір.укр";

        mockMvc.perform(post("/api/v1/url")
                        .param("urlText", idnUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists());
    }

}
