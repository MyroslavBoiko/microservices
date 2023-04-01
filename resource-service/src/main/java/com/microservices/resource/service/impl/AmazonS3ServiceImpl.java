package com.microservices.resource.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.microservices.resource.entity.FileMeta;
import com.microservices.resource.service.AmazonS3Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private AmazonS3 amazonS3;

    @Override
    public PutObjectResult upload(String key, MultipartFile file) {
        log.debug("Key: " + key + ", FileName:" + file.getOriginalFilename());

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            log.error("Exception while trying to get stream of data.", e);
            throw new RuntimeException(e);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(bytes.length);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        return amazonS3.putObject(bucketName, key, byteArrayInputStream, objectMetadata);
    }

    public S3Object download(FileMeta fileMeta, HttpRange range) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileMeta.getKey());
        getObjectRequest.withRange(range.getRangeStart(fileMeta.getSize()), range.getRangeEnd(fileMeta.getSize()));
        return amazonS3.getObject(getObjectRequest);
    }

    @Override
    public void delete(List<String> keys) {
        List<DeleteObjectsRequest.KeyVersion> keysList = keys.stream().map(DeleteObjectsRequest.KeyVersion::new).toList();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.setKeys(keysList);
        amazonS3.deleteObjects(deleteObjectsRequest);
    }
}
