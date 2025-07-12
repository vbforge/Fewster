package com.vladproduction.fewster.repository;

import com.vladproduction.fewster.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    Optional<UrlEntity> findByShortUrl(String shortUrl);

    Optional<UrlEntity> findByOriginalUrl(String originalUrl);

    boolean existsByShortUrl(String shortUrl);

}
