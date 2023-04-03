package com.microservices.resource.service;

import org.springframework.http.HttpRange;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceProcessingService {
    long upload(MultipartFile file);
    byte[] download(long id, HttpRange range);
    List<Long> deleteByIds(List<Long> ids);

}
