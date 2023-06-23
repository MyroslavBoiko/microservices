package com.microservices.resource.service;

import com.microservices.resource.domain.StorageDto;

public interface StorageService {
    StorageDto getStagingDetails();

    StorageDto getPermanentDetails();
}
