package com.microservices.songservice.repository;

import com.microservices.songservice.entity.SongMetadata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SongMetadataRepository extends CrudRepository<SongMetadata, Long> {
    Optional<SongMetadata> findByResourceId(Long resourceId);

    List<SongMetadata> deleteAllByIdIn(List<Long> ids);
}
