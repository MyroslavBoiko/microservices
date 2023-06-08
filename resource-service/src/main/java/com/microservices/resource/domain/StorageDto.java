package com.microservices.resource.domain;

import lombok.Builder;


public record StorageDto(int id, String storageType, String bucket, String path) {
}
