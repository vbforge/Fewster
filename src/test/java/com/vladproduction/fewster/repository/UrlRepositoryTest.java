package com.vladproduction.fewster.repository;

import com.vladproduction.fewster.entity.UrlEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    @DisplayName("findByShortUrl() should return correct entity")
    void testFindByShortUrl() {
        UrlEntity saved = new UrlEntity(null, "https://original.com", "short123");
        urlRepository.save(saved);

        Optional<UrlEntity> found = urlRepository.findByShortUrl("short123");
        assertTrue(found.isPresent());
        assertEquals("https://original.com", found.get().getOriginalUrl());
    }

    @Test
    @DisplayName("findByOriginalUrl() should return correct entity")
    void testFindByOriginalUrl() {
        UrlEntity saved = new UrlEntity(null, "https://another.com", "xyz789");
        urlRepository.save(saved);

        Optional<UrlEntity> found = urlRepository.findByOriginalUrl("https://another.com");
        assertTrue(found.isPresent());
        assertEquals("xyz789", found.get().getShortUrl());
    }

    @Test
    @DisplayName("existsByShortUrl() should return true if shortUrl exists")
    void testExistsByShortUrl() {
        UrlEntity saved = new UrlEntity(null, "https://exists.com", "abc999");
        urlRepository.save(saved);

        boolean exists = urlRepository.existsByShortUrl("abc999");
        assertTrue(exists);
    }

    @Test
    @DisplayName("Short URL must be unique - constraint test")
    void testUniqueShortUrlConstraint() {
        UrlEntity entity1 = new UrlEntity(null, "https://first.com", "dup123");
        UrlEntity entity2 = new UrlEntity(null, "https://second.com", "dup123");

        urlRepository.save(entity1);
        assertThrows(Exception.class, () -> {
            urlRepository.saveAndFlush(entity2); // flush to trigger constraint violation
        });
    }

    @Test
    @DisplayName("Original URL and short URL must not be null")
    void testNotNullConstraints() {
        UrlEntity missingOriginal = new UrlEntity(null, null, "nonnull1");
        UrlEntity missingShort = new UrlEntity(null, "https://valid.com", null);

        assertThrows(Exception.class, () -> urlRepository.saveAndFlush(missingOriginal));
        assertThrows(Exception.class, () -> urlRepository.saveAndFlush(missingShort));
    }

}