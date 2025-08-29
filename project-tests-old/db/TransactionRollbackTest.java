package com.vladproduction.fewster.db;

import com.vladproduction.fewster.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("constraints")
@Transactional
public class TransactionRollbackTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void shouldRollbackOnException() {
        long countBefore = urlRepository.count();

        assertThrows(Exception.class, () -> urlRepository.saveAndFlush(null));

        long countAfter = urlRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }

}
