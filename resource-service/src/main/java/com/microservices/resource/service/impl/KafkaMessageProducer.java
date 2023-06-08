package com.microservices.resource.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microservices.resource.exception.UploadEventException;
import com.microservices.resource.service.AmazonS3Service;
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
    private final AmazonS3Service amazonS3Service;

    @Retryable(value = RuntimeException.class, maxAttempts = 4,
            backoff = @Backoff(maxDelay = 1000L, multiplier = 2), listeners = {"retryListener"})
    public void sendMessage(Long id) {
        String msg = buildMessage(id);

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

    private String buildMessage(Long id) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("resourceId", id);
        String message;
        try {
            message = objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Recover
    public void recover(Exception ex, String msg) {
        throw new UploadEventException("Unable to post upload event message: " + msg);
    }


}
