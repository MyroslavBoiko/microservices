package com.microservices.storage.controller;

import com.microservices.storage.payload.CreateStorageResponse;
import com.microservices.storage.payload.DeleteStoragesResponse;
import com.microservices.storage.payload.StorageDto;
import com.microservices.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
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
public class StorageController {

    private final StorageService storageService;
    @PostMapping
    public CreateStorageResponse createStorage(@RequestBody StorageDto storageDto) {
        int id = storageService.createStorage(storageDto);
        return new CreateStorageResponse(id);
    }

    @GetMapping("/{storageType}")
    public StorageDto getStorageByType(@PathVariable(value = "storageType") String storageType) {
        return storageService.getStorageByType(storageType);
    }

    @GetMapping
    public List<StorageDto> getStoragesList() {
        return storageService.getStorages();
    }

    @DeleteMapping
    public DeleteStoragesResponse deleteStorages(@RequestParam(name = "id") List<Integer> ids) {
        List<Integer> deletedStorages = storageService.deleteStorages(ids);
        return new DeleteStoragesResponse(deletedStorages);
    }

}
