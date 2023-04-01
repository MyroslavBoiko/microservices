package com.microservices.resource.service.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.microservices.resource.entity.FileMeta;
import com.microservices.resource.exception.FileMetaNotFoundException;
import com.microservices.resource.repository.FileMetaRepository;
import com.microservices.resource.service.AmazonS3Service;
import com.microservices.resource.service.ResourceProcessingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ResourceProcessingServiceImpl implements ResourceProcessingService {

    private AmazonS3Service amazonS3Service;
    private FileMetaRepository fileMetaRepository;

    @Transactional
    @Override
    public long upload(MultipartFile file) {
        String fileName = String.format("%s", file.getOriginalFilename());
        String key = String.format("%s-%s", fileName, UUID.randomUUID());

        // Saving metadata to db
        FileMeta fileMeta = new FileMeta(fileName, key, file.getSize());
        FileMeta saved = fileMetaRepository.save(fileMeta);

        // Uploading file to s3
        amazonS3Service.upload(key, file);

        return saved.getId();
    }

    @Override
    public byte[] download(long id, HttpRange range) {
        FileMeta fileMeta = fileMetaRepository.findById(id).orElseThrow(FileMetaNotFoundException::new);
        try (S3Object s3Object = amazonS3Service.download(fileMeta, range)) {
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            log.error("Exception while trying to get stream of data.", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public List<Long> deleteByIds(List<Long> ids) {
        record IdToKey(Long id, String key){}

        List<FileMeta> deleteAllByIdIn = fileMetaRepository.deleteAllByIdIn(ids);
        if (deleteAllByIdIn.isEmpty()) {
            return Collections.emptyList();
        }
        List<IdToKey> idToKeys = deleteAllByIdIn.stream()
                .map(fileMeta -> new IdToKey(fileMeta.getId(), fileMeta.getKey()))
                .toList();

        amazonS3Service.delete(idToKeys.stream().map(IdToKey::key).toList());

        return idToKeys.stream().map(IdToKey::id).toList();
    }
}
