package com.microservices.storage.payload;

import com.microservices.storage.domain.StorageType;
import lombok.Builder;


@Builder
public record StorageDto(int id, String storageType, String bucket, String path) {
}
