package com.microservices.resource.service.impl;

import com.microservices.resource.exception.UploadEventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.microservices.resource.config.KafkaTopicConfig.RESOURCES_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Retryable(retryFor = RuntimeException.class, maxAttempts = 4,
            backoff = @Backoff(maxDelay = 1000L, multiplier = 2), listeners = {"retryListener"})
    public void sendMessage(String msg) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(RESOURCES_TOPIC, msg);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[" + msg +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to send message=[" +
                        msg + "] due to : " + ex.getMessage());
            }
        });
    }
    @Recover
    public void recover(Exception ex, String msg) {
        throw new UploadEventException("Unable to post upload event message: " + msg);
    }


}
