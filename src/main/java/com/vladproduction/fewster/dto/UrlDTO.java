package com.vladproduction.fewster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlDTO {

    private Long id;

    @NotBlank(message = "Original URL is required")
    @Size(max = 2048, message = "URL is too long")
    private String originalUrl;

    private String shortUrl;


}
