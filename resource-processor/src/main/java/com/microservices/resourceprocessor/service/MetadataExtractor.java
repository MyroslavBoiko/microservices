package com.microservices.resourceprocessor.service;

import com.microservices.resourceprocessor.dto.SongDto;

public interface MetadataExtractor {
    SongDto extractSongMetadata(byte[] file, Long id);
}
