package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.mapper.UrlMapper;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.service.UrlService;
import com.vladproduction.fewster.utility.GlobalUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UrlServiceImpl implements UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);

    private final GlobalUtility globalUtility;
    private final UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository, GlobalUtility globalUtility) {
        this.urlRepository = urlRepository;
        this.globalUtility = globalUtility;
    }

    @Override
    public List<UrlDTO> createAll(List<String> listUrlsText) {
        log.info("Attempting to create and save list of URLs: {}", listUrlsText);

        //validate URLs format
        for (String potentialUrl : listUrlsText) {
            if (globalUtility.isValidUrl(potentialUrl)) {
                throw new IllegalArgumentException("Invalid Url format: " + potentialUrl);
            }
        }

        //separate existing and new URLs
        List<UrlDTO> results = new ArrayList<>();
        List<UrlEntity> newEntities = new ArrayList<>();

        for (String potentialUrl : listUrlsText) {
            Optional<UrlEntity> existingUrl = urlRepository.findByOriginalUrl(potentialUrl);
            if (existingUrl.isPresent()) {
                log.info("URL already exists: {}", potentialUrl);
                results.add(UrlMapper.toDTO(existingUrl.get()));
            } else {
                // Create entity for new URL
                String shortUrl = globalUtility.generateUniqueShortUrl(potentialUrl);
                UrlEntity urlEntity = new UrlEntity();
                urlEntity.setOriginalUrl(potentialUrl);
                urlEntity.setShortUrl(shortUrl);
                newEntities.add(urlEntity);
            }
        }

        //save all new entities at once
        if (!newEntities.isEmpty()) {
            List<UrlEntity> savedEntities = urlRepository.saveAll(newEntities);
            savedEntities.forEach(entity -> results.add(UrlMapper.toDTO(entity)));
            log.info("Successfully saved {} new URLs", savedEntities.size());
        }

        return results;
    }

    @Override
    public UrlDTO create(String urlText) {
        log.info("Attempting to create and save url: {}", urlText);

        //validate URL format
        if(globalUtility.isValidUrl(urlText)){
            throw new IllegalArgumentException("Invalid Url format: " + urlText);
        }

        //check for duplicates (if url already exists in database)
        //if exists: returning existing short URL
        Optional<UrlEntity> existingUrl = urlRepository.findByOriginalUrl(urlText);
        if(existingUrl.isPresent()){
            log.info("URL already exists, returning existing short URL: {}", existingUrl.get().getShortUrl());
            return UrlMapper.toDTO(existingUrl.get());
        }

        String shortUrl = globalUtility.generateUniqueShortUrl(urlText);

        log.info("Creating URL Entity where original Url: {} and short Url: {}", urlText, shortUrl);
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(urlText);
        urlEntity.setShortUrl(shortUrl);

        log.info("Attempting to save url: {} ", urlEntity);
        UrlEntity entity = urlRepository.save(urlEntity);
        log.info("Saved successfully to database");

        return UrlMapper.toDTO(entity);
    }

    @Override
    public UrlDTO getById(Long id) {
        log.info("Attempting to get url from database by id: {}", id);
        if (id <= 0) {
            throw new IllegalArgumentException("Id should be positive");
        }

        UrlEntity entity = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Url with id: " + id + " not found."));

        log.info("Url: {} found by id: {}", entity, id);

        return UrlMapper.toDTO(entity);
    }

    @Override
    public UrlDTO getByShortUrl(String shortUrl) {
        log.info("Attempting to get url from database by short Url: {}", shortUrl);
        if(shortUrl == null || shortUrl.isEmpty()){
            throw new IllegalArgumentException("Short URL should not be empty");
        }

        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL with short URL: " + shortUrl + " not found."));

        log.info("URL: {} found by short URL: {}", urlEntity, shortUrl);

        return UrlMapper.toDTO(urlEntity);
    }

    @Override
    public List<UrlDTO> getAll() {
        log.info("Attempt to retrieving all urls from database");
        try {
            List<UrlEntity> entityList = urlRepository.findAll();
            log.info("Successfully retrieved {} urls", entityList.size());
            return entityList
                    .stream()
                    .map(UrlMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            //throwing exception or logging error and return empty list
            log.error("An error occurred while retrieving all urls.", e);
            return List.of();
        }
    }

}
