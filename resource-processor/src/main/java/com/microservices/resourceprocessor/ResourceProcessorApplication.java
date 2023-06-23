package com.microservices.resourceprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
public class ResourceProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceProcessorApplication.class, args);
    }

}
