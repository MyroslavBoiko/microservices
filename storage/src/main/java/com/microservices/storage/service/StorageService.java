package com.microservices.storage.service;

import com.microservices.storage.domain.Storage;
import com.microservices.storage.payload.StorageDto;
import com.microservices.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;

    public int createStorage(StorageDto storageDto) {
        Storage storage = Storage.builder()
                .storageType(storageDto.storageType())
                .bucket(storageDto.bucket())
                .path(storageDto.path())
                .build();
        Storage saved = storageRepository.save(storage);
        return saved.getId();
    }

    public List<StorageDto> getStorages() {
        List<Storage> storages = storageRepository.findAll();
        return storages.stream().map(this::toStorageDto)
                .toList();
    }

    public List<Integer> deleteStorages(List<Integer> ids) {
        List<Storage> allById = storageRepository.findAllById(ids);
        storageRepository.deleteAll(allById);
        return allById.stream().map(Storage::getId).toList();
    }

    public StorageDto getStorageByType(String storageType) {
        Storage storage = storageRepository.findStorageByStorageType(storageType);
        return toStorageDto(storage);
    }

    private StorageDto toStorageDto(Storage storage) {
        return StorageDto.builder()
                .id(storage.getId())
                .bucket(storage.getBucket())
                .storageType(storage.getStorageType())
                .path(storage.getPath()).build();
    }
}
