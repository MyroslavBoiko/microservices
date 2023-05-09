package com.microservices.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.resource.service.ResourceProcessingService;
import com.microservices.resource.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ResourceProcessingService resourceProcessingService;
    @MockBean
    private ValidationService validationService;

    @Test
    void uploadResourceReturnIdOfResource() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Hello, World!".getBytes()
        );

        when(resourceProcessingService.upload(file)).thenReturn(1L);

        mockMvc.perform(multipart("/resources").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1}")));
    }

    @Test
    void findByIdReturnBytesOfData() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Hello, World!".getBytes()
        );

        when(resourceProcessingService.download(1L, HttpRange.createByteRange(0))).thenReturn(file.getBytes());

        this.mockMvc.perform(get("/resources/{id}", 1L)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .content(file.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    void deleteByIds() throws Exception {
        when(resourceProcessingService.deleteByIds(List.of(1L))).thenReturn(List.of(1L));
        this.mockMvc.perform(delete("/resources")
                        .queryParam("id", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"ids\":[1]}"));
    }
}