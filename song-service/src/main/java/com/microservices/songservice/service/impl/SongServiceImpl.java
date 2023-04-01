package com.microservices.songservice.service.impl;

import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.entity.SongMetadata;
import com.microservices.songservice.exception.DuplicateResourceIdException;
import com.microservices.songservice.exception.SongMetadataNotFound;
import com.microservices.songservice.mapper.SongMetadataMapper;
import com.microservices.songservice.repository.SongMetadataRepository;
import com.microservices.songservice.service.SongService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    private final SongMetadataRepository songMetadataRepository;
    private final SongMetadataMapper songMetadataMapper;
    @Override
    public long create(SongDto songDto) {
        SongMetadata songMetadata = songMetadataMapper.songDtoToSongMetadata(songDto);
        songMetadataRepository.findByResourceId(songMetadata.getResourceId()).ifPresent(s -> {
            throw new DuplicateResourceIdException("Record with resourceId=%s already exists."
                    .formatted(songMetadata.getResourceId()));
        });
        SongMetadata saved = songMetadataRepository.save(songMetadata);
        return saved.getId();
    }

    @Override
    public SongDto findById(long id) {
        SongMetadata songMetadata = songMetadataRepository.findById(id).orElseThrow(SongMetadataNotFound::new);
        return songMetadataMapper.songMetadataToSongDto(songMetadata);
    }

    @Override
    @Transactional
    public List<Long> deleteByIds(List<Long> ids) {
        return songMetadataRepository.deleteAllByIdIn(ids)
                .stream()
                .map(SongMetadata::getId)
                .toList();
    }
}
