package com.microservices.storage.controller;

import com.microservices.storage.payload.CreateStorageResponse;
import com.microservices.storage.payload.DeleteStoragesResponse;
import com.microservices.storage.payload.StorageDto;
import com.microservices.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
@Slf4j
public class StorageController {

    private final StorageService storageService;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CreateStorageResponse createStorage(@RequestBody StorageDto storageDto) {
        log.info("Create storage request");
        int id = storageService.createStorage(storageDto);
        return new CreateStorageResponse(id);
    }

    @GetMapping("/{storageType}")
    public StorageDto getStorageByType(@PathVariable(value = "storageType") String storageType) {
        log.info("Get storage by type request for storage type {}", storageType);
        return storageService.getStorageByType(storageType);
    }

    @GetMapping
    public List<StorageDto> getStoragesList() {
        return storageService.getStorages();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DeleteStoragesResponse deleteStorages(@RequestParam(name = "id") List<Integer> ids) {
        List<Integer> deletedStorages = storageService.deleteStorages(ids);
        return new DeleteStoragesResponse(deletedStorages);
    }

}
