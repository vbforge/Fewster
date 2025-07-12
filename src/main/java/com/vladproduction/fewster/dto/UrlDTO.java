package com.vladproduction.fewster.dto;

public class UrlDTO {

    private Long id;
    private String originalUrl;
    private String shortUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @Override
    public String toString() {
        return String.format("UrlDTO id: %d, original: %s, short: %s", id, originalUrl, shortUrl);
    }

}
