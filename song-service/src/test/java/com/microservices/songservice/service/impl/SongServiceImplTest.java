package com.microservices.songservice.service.impl;

import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.entity.SongMetadata;
import com.microservices.songservice.exception.SongMetadataNotFound;
import com.microservices.songservice.mapper.SongMetadataMapper;
import com.microservices.songservice.repository.SongMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock
    private SongMetadataRepository songMetadataRepository;
    @InjectMocks
    private SongServiceImpl songService;
    @Spy
    private SongMetadataMapper songMetadataMapper = Mappers.getMapper(SongMetadataMapper.class);

    @Test
    void createSongShouldReturnId() {
        SongDto songDto = new SongDto("Name", "Artist", "Album", "4:22", 1L, (short) 1999);
        when(songMetadataRepository.findByResourceId(1L))
                .thenReturn(Optional.empty());
        SongMetadata songMetadata = new SongMetadata();
        songMetadata.setId(1L);
        when(songMetadataRepository.save(any(SongMetadata.class))).thenReturn(songMetadata);

        long id = songService.create(songDto);

        assertEquals(1L, id);
    }

    @Test
    void findByIdShouldReturnSongData() {
        SongMetadata songMetadata = new SongMetadata();
        songMetadata.setResourceId(1L);
        songMetadata.setName("Name");
        when(songMetadataRepository.findById(1L)).thenReturn(Optional.of(songMetadata));

        SongDto songDto = songService.findById(1L);

        assertEquals("Name", songDto.getName());
        assertEquals(1L, songDto.getResourceId());
    }

    @Test
    void findByIdEntityNotFound() {
        when(songMetadataRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SongMetadataNotFound.class, () -> songService.findById(1L));
    }

    @Test
    void deleteByIdsShouldReturnDeletedIds() {
        List<Long> ids = List.of(1L);
        SongMetadata songMetadata = new SongMetadata();
        songMetadata.setId(1L);
        when(songMetadataRepository.deleteAllByIdIn(ids)).thenReturn(List.of(songMetadata));
        List<Long> deletedIds = songService.deleteByIds(ids);

        assertEquals(1, deletedIds.size());
        assertEquals(1L, deletedIds.get(0));
    }
}