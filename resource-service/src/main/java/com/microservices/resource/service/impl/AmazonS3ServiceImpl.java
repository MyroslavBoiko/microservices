package com.microservices.resource.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.microservices.resource.domain.StorageDto;
import com.microservices.resource.entity.FileMeta;
import com.microservices.resource.entity.StorageStatus;
import com.microservices.resource.service.AmazonS3Service;
import com.microservices.resource.service.StorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.microservices.resource.service.impl.ResourceProcessingServiceImpl.createBaseKey;
import static com.microservices.resource.service.impl.ResourceProcessingServiceImpl.getFullKey;

@Service
@Slf4j
@AllArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private AmazonS3 amazonS3;
    private StorageService storageService;

    private PutObjectResult upload(String bucketName, String key, MultipartFile file) {
        log.info("Bucket: " + bucketName + ", Key: " + key + ", FileName: " + file.getOriginalFilename());

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

    @Override
    public S3Object download(FileMeta fileMeta, HttpRange range) {
        StorageDto storageDetails;
        if (StorageStatus.STAGING == fileMeta.getStatus()) {
            storageDetails = storageService.getStagingDetails();
        } else {
            storageDetails = storageService.getPermanentDetails();
        }
        GetObjectRequest getObjectRequest = new GetObjectRequest(storageDetails.bucket(), fileMeta.getKey());
        getObjectRequest.withRange(range.getRangeStart(fileMeta.getSize()), range.getRangeEnd(fileMeta.getSize()));
        return amazonS3.getObject(getObjectRequest);
    }

    @Override
    public String moveFileToPermanentStorage(FileMeta fileMeta) {
        StorageDto stagingDetails = storageService.getStagingDetails();
        StorageDto permanentDetails = storageService.getPermanentDetails();

        String destinationKey = getFullKey(permanentDetails.path(), createBaseKey(fileMeta.getFileName()));
        amazonS3.copyObject(stagingDetails.bucket(),
                fileMeta.getKey(),
                permanentDetails.bucket(),
                destinationKey);
        amazonS3.deleteObject(stagingDetails.bucket(), fileMeta.getKey());
        return destinationKey;
    }

    @Override
    public void saveToStaging(String bucket, String key, MultipartFile file) {
        // Uploading file to s3
        upload(bucket, key, file);
    }

    @Override
    public void delete(List<String> keys) {
        StorageDto permanentDetails = storageService.getPermanentDetails();
        List<DeleteObjectsRequest.KeyVersion> keysList = keys.stream()
                .map(DeleteObjectsRequest.KeyVersion::new)
                .toList();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(permanentDetails.bucket());
        deleteObjectsRequest.setKeys(keysList);
        amazonS3.deleteObjects(deleteObjectsRequest);
    }

}
