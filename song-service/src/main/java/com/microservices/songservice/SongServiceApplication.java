package com.microservices.songservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SongServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongServiceApplication.class, args);
    }

}
