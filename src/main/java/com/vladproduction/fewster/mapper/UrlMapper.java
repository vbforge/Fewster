package com.vladproduction.fewster.mapper;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;

public class UrlMapper {

    //Entity -> DTO
    public static UrlDTO toDTO(UrlEntity entity){
        if(entity == null){
            return null;
        }
        UrlDTO urlDTO = new UrlDTO();
        urlDTO.setId(entity.getId());
        urlDTO.setOriginalUrl(entity.getOriginalUrl());
        urlDTO.setShortUrl(entity.getShortUrl());
        return urlDTO;
    }

    //DTO -> Entity
    public static UrlEntity toEntity(UrlDTO dto){
        if(dto == null){
            return null;
        }
        UrlEntity entity = new UrlEntity();
        entity.setId(dto.getId());
        entity.setOriginalUrl(dto.getOriginalUrl());
        entity.setShortUrl(dto.getShortUrl());
        return entity;
    }

}
