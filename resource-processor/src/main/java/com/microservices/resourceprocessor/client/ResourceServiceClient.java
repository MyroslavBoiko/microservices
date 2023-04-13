package com.microservices.resourceprocessor.client;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/resources")
public interface ResourceServiceClient {

    @GetExchange(value = "{id}", accept = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] findById(@PathVariable long id,
                     @RequestHeader(value = HttpHeaders.RANGE, required = false) String range);

}
