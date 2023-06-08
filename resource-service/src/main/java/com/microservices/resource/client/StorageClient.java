package com.microservices.resource.client;

import com.microservices.resource.domain.StorageDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/storages")
public interface StorageClient {
    @GetExchange(value = "{storageType}")
    StorageDto getStorageByType(@PathVariable String storageType);
}
