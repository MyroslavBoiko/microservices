package com.microservices.songservice.mapper;

import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.entity.SongMetadata;
import org.mapstruct.Mapper;

@Mapper
public interface SongMetadataMapper {

    SongMetadata songDtoToSongMetadata(SongDto songDto);

    SongDto songMetadataToSongDto(SongMetadata songMetadata);
}
