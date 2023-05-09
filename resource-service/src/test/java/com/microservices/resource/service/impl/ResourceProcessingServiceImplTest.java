package com.microservices.resource.service.impl;

import com.amazonaws.http.apache.request.impl.HttpGetWithBody;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.microservices.resource.entity.FileMeta;
import com.microservices.resource.repository.FileMetaRepository;
import com.microservices.resource.service.AmazonS3Service;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceProcessingServiceImplTest {

    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private FileMetaRepository fileMetaRepository;
    @Mock
    private KafkaMessageProducer kafkaMessageProducer;
    @InjectMocks
    private ResourceProcessingServiceImpl resourceProcessingService;

    @Test
    void uploadFileAndReturnId() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Hello, World!".getBytes()
        );
        FileMeta fileMeta = new FileMeta();
        fileMeta.setId(1L);

        when(fileMetaRepository.save(ArgumentMatchers.any())).thenReturn(fileMeta);

        long upload = resourceProcessingService.upload(file);

        assertEquals(1L, upload);
    }

    @Test
    void downloadResourceReturnBytes() {
        FileMeta fileMeta = new FileMeta();
        when(fileMetaRepository.findById(1L)).thenReturn(Optional.of(fileMeta));
        HttpRange range = HttpRange.createByteRange(0);
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream(new byte[] {1}), new HttpGetWithBody("url")));

        when(amazonS3Service.download(fileMeta, range)).thenReturn(s3Object);

        byte[] downloaded = resourceProcessingService.download(1L, range);

        assertEquals(1, downloaded.length);
    }

    @Test
    void deleteByIdsReturnDeletedIds() {
        List<Long> ids = List.of(1L);
        FileMeta fileMeta = new FileMeta();
        fileMeta.setId(1L);

        when(fileMetaRepository.deleteAllByIdIn(ids)).thenReturn(List.of(fileMeta));

        List<Long> deletedIds = resourceProcessingService.deleteByIds(ids);

        assertEquals(1L, deletedIds.get(0));
    }
}