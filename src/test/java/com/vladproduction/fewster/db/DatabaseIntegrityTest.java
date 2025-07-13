package com.vladproduction.fewster.db;

import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("constraints")
@Transactional
public class DatabaseIntegrityTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void shouldPreventNullOriginalUrl() {
        UrlEntity entity = new UrlEntity();
        entity.setShortUrl("short123");

        assertThatThrownBy(() -> urlRepository.saveAndFlush(entity))
                .isInstanceOf(Exception.class); // Could be ConstraintViolation or Persistence
    }

    @Test
    void shouldPreventDuplicateShortUrl() {
        urlRepository.save(new UrlEntity("https://a.com", "shortXYZ"));
        UrlEntity duplicate = new UrlEntity("https://b.com", "shortXYZ");

        assertThatThrownBy(() -> urlRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldSetTimestampsCorrectly() {
        UrlEntity entity = new UrlEntity("https://example.com", "shortABC");
        UrlEntity saved = urlRepository.save(entity);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

}
