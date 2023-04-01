package com.microservices.songservice.service;

import com.microservices.songservice.dto.SongDto;

import java.util.List;

public interface SongService {
    long create(SongDto songDto);

    SongDto findById(long id);

    List<Long> deleteByIds(List<Long> ids);
}
