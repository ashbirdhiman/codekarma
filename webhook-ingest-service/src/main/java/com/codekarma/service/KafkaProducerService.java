package com.codekarma.service;


import com.codekarma.dto.NormalizedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();


    @Value("${github.ingest.topic:events.ingest}")
    private String topic;


    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(NormalizedEvent event) {
        try {
            String json = mapper.writeValueAsString(event);
            logger.info("🎯 Publishing event to topic '{}': {}", topic, json);
            kafkaTemplate.send(topic, event.getEventId(), json);
            logger.info("✅ Event published successfully with ID: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("❌ Failed to publish event with ID: {}", event.getEventId(), e);
            // Don't throw exception - let webhook succeed even if Kafka is down
        }
    }
}