package com.microservices.resourceprocessor.service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.resourceprocessor.client.ResourceServiceClient;
import com.microservices.resourceprocessor.client.SongServiceClient;
import com.microservices.resourceprocessor.dto.SongDto;
import com.microservices.resourceprocessor.service.MetadataExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    record Message(String resourceId){}

    private static final String TOPIC = "resources";

    private final SongServiceClient songServiceClient;
    private final ResourceServiceClient resourceServiceClient;
    private final MetadataExtractor metadataExtractor;

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void processResourceCreating(String message) {
        log.info("Received Message: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        Message consumedMessage;
        try {
            consumedMessage = objectMapper.readValue(message, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        long id = Long.parseLong(consumedMessage.resourceId());
        byte[] byId = resourceServiceClient.findById(id, "");
        SongDto songDto = metadataExtractor.extractSongMetadata(byId, id);
        log.info("Metadata extracted from byte array");
        songServiceClient.create(songDto);
        log.info("Song metadata sent to song service");
    }



}
