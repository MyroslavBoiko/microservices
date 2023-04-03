package com.microservices.resource.service;

import org.springframework.web.multipart.MultipartFile;

public interface ValidationService {

    void validateFile(MultipartFile file);
    void validateRange(String range);
}
