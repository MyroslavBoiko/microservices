package com.microservices.resourceprocessor.client;

import com.microservices.resourceprocessor.dto.SongDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        url = "/songs",
        accept = MediaType.APPLICATION_JSON_VALUE)
public interface SongServiceClient {
    record SongCreatedResponse(long id) {}
    @PostExchange
    SongCreatedResponse create(@RequestBody SongDto songDto);
}
