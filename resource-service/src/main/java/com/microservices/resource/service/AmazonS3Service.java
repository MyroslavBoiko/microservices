package com.microservices.resource.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.microservices.resource.entity.FileMeta;
import org.springframework.http.HttpRange;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AmazonS3Service {
    PutObjectResult upload(String key, MultipartFile file);

    S3Object download(FileMeta fileMeta, HttpRange range);

    void delete(List<String> keys);
}
