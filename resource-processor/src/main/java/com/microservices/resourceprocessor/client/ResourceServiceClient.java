package com.microservices.resourceprocessor.client;


import com.microservices.resourceprocessor.dto.SongDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/resources")
public interface ResourceServiceClient {

    @GetExchange(value = "{id}", accept = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] findById(@PathVariable long id,
                     @RequestHeader(value = HttpHeaders.RANGE, required = false) String range);

    @PostExchange("/permanent")
    ResponseEntity<?> moveToPermanent(@RequestBody Long id);
}
