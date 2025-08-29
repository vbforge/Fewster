package com.vladproduction.fewster.db;

import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("performance")
@Transactional
public class PerformanceIndexTest {

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void populateData() {
        IntStream.range(0, 1000).forEach(i -> {
            urlRepository.save(new UrlEntity("https://example.com/" + i, "short" + i));
        });
    }

    @Test
    void shouldQueryFastWithIndex() {
        long start = System.currentTimeMillis();
        var entity = urlRepository.findByShortUrl("short999");

        long end = System.currentTimeMillis();

        assertThat(entity).isPresent();
        assertThat(end - start).isLessThan(100); // < 100ms
    }

}
