package com.microservices.resource.service.impl;

import com.microservices.resource.client.StorageClient;
import com.microservices.resource.domain.StorageDto;
import com.microservices.resource.service.StorageService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    @Value("${storage.type.staging}")
    private String storageStaging;
    @Value("${storage.type.permanent}")
    private String storagePermanent;

    private final StorageClient storageClient;

    @Override
    @CircuitBreaker(name = "staging", fallbackMethod = "fallbackForStaging")
    public StorageDto getStagingDetails() {
        StorageDto staging = storageClient.getStorageByType(storageStaging);
        log.info("Staging storage details received {}", staging);
        return staging;
    }

    @Override
    @CircuitBreaker(name = "permanent", fallbackMethod = "fallbackForPermanent")
    public StorageDto getPermanentDetails() {
        StorageDto permanent = storageClient.getStorageByType(storagePermanent);
        log.info("Permanent storage details received {}", permanent);
        return permanent;
    }

    private StorageDto fallbackForStaging(Throwable throwable) {
        StorageDto storageStub = new StorageDto(1, "STAGING", "staging", "/stag");
        log.info("Fallback method called for Staging storage {}", storageStub);
        return storageStub;
    }

    private StorageDto fallbackForPermanent(Throwable throwable) {
        StorageDto storageStub = new StorageDto(2, "PERMANENT", "permanent", "/perm");
        log.info("Fallback method called for Permanent storage {}", storageStub);
        return storageStub;
    }
}
