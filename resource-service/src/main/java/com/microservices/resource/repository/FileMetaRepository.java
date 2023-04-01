package com.microservices.resource.repository;

import com.microservices.resource.entity.FileMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileMetaRepository extends CrudRepository<FileMeta, Long> {
    List<FileMeta> deleteAllByIdIn(List<Long> ids);
}
