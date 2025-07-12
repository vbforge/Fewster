package com.vladproduction.fewster.service;

import com.vladproduction.fewster.dto.UrlDTO;

import java.util.List;

public interface UrlService {

    List<UrlDTO> createAll(List<String> listUrlsText);

    UrlDTO create(String urlText);

    UrlDTO getById(Long id);

    UrlDTO getByShortUrl(String shortUrl);

    List<UrlDTO> getAll();

}
