package com.microservices.storage.repository;

import com.microservices.storage.domain.Storage;
import org.springframework.data.repository.ListCrudRepository;

public interface StorageRepository extends ListCrudRepository<Storage, Integer> {
    Storage findStorageByStorageType(String type);
}
