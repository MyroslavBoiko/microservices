package com.microservices.resourceprocessor.service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.resourceprocessor.client.ResourceServiceClient;
import com.microservices.resourceprocessor.client.SongServiceClient;
import com.microservices.resourceprocessor.dto.SongDto;
import com.microservices.resourceprocessor.service.MetadataExtractor;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    record Message(String resourceId, String traceId, String spanId){}

    private static final String TOPIC = "resources";

    private final SongServiceClient songServiceClient;
    private final ResourceServiceClient resourceServiceClient;
    private final MetadataExtractor metadataExtractor;
    private final Tracer tracer;

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void processResourceCreating(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        Message consumedMessage;
        try {
            consumedMessage = objectMapper.readValue(message, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        setTraceContext(consumedMessage);
        log.info("Received Message: " + message);

        long id = Long.parseLong(consumedMessage.resourceId());
        byte[] byId = resourceServiceClient.findById(id, "");
        SongDto songDto = metadataExtractor.extractSongMetadata(byId, id);
        log.info("Metadata extracted from byte array");
        songServiceClient.create(songDto);
        log.info("Song metadata sent to song service {}", songDto);
        resourceServiceClient.moveToPermanent(id);
        log.info("Song metadata moved to permanent storage {}", songDto);
    }

    private void setTraceContext(Message consumedMessage) {
        TraceContext traceContext = tracer.traceContextBuilder()
                .traceId(consumedMessage.traceId())
                .spanId(consumedMessage.spanId())
                .build();
        tracer.currentTraceContext().newScope(traceContext);
    }


}
