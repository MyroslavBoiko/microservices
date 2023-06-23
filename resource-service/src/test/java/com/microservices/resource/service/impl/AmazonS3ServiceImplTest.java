package com.microservices.resource.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.microservices.resource.domain.StorageDto;
import com.microservices.resource.entity.FileMeta;
import com.microservices.resource.entity.StorageStatus;
import com.microservices.resource.service.StorageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private AmazonS3ServiceImpl amazonS3Service;

    @Test
    void uploadToBucket() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Hello, World!".getBytes()
        );

        amazonS3Service.saveToStaging("test", "key", file);

        verify(amazonS3).putObject(eq("test"), eq("key"), any(), any());
    }

    @Test
    void downloadFromBucket() {
        when(storageService.getPermanentDetails())
                .thenReturn(new StorageDto(2, StorageStatus.PERMANENT.toString(), "permanent", "/perm"));

        HttpRange range = HttpRange.createByteRange(0);

        FileMeta fileMeta = new FileMeta();
        fileMeta.setKey("key");
        fileMeta.setSize(100L);
        fileMeta.setStatus(StorageStatus.PERMANENT);

        GetObjectRequest getObjectRequest = new GetObjectRequest("permanent", fileMeta.getKey());
        getObjectRequest.withRange(range.getRangeStart(fileMeta.getSize()), range.getRangeEnd(fileMeta.getSize()));
        S3Object s3Object = new S3Object();

        when(amazonS3.getObject(getObjectRequest)).thenReturn(s3Object);

        S3Object downloaded = amazonS3Service.download(fileMeta, range);

        assertEquals(s3Object, downloaded);

    }

    @Test
    void deleteFromBucket() {
        when(storageService.getPermanentDetails())
                .thenReturn(new StorageDto(2, StorageStatus.PERMANENT.toString(), "permanent", "/perm"));

        List<String> keys = List.of("file1", "file2");

        amazonS3Service.delete(keys);

        ArgumentCaptor<DeleteObjectsRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(amazonS3).deleteObjects(argumentCaptor.capture());

        assertEquals("permanent", argumentCaptor.getValue().getBucketName());
        assertEquals(keys.get(0), argumentCaptor.getValue().getKeys().get(0).getKey());
        assertEquals(keys.get(1), argumentCaptor.getValue().getKeys().get(1).getKey());
    }
}