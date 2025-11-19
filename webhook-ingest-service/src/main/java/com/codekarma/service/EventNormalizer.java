package com.codekarma.service;


import com.codekarma.dto.NormalizedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.UUID;


@Component
public class EventNormalizer {


    private final ObjectMapper mapper = new ObjectMapper();


    public NormalizedEvent normalize(String eventType, JsonNode payload) {
        String action = payload.has("action") ? payload.get("action").asText() : null;
        String repo = null;
        if (payload.has("repository") && payload.get("repository").has("full_name")) {
            repo = payload.get("repository").get("full_name").asText();
        }
        String sender = null;
        if (payload.has("sender") && payload.get("sender").has("login")) {
            sender = payload.get("sender").get("login").asText();
        }


        String raw = null;
        try {
            raw = mapper.writeValueAsString(payload);
        } catch (Exception ignored) {}


        NormalizedEvent e = new NormalizedEvent();
        e.setEventId(UUID.randomUUID().toString());
        e.setEventType(eventType);
        e.setAction(action);
        e.setRepo(repo);
        e.setSender(sender);
        e.setTimestamp(Instant.now().toEpochMilli());
        e.setRawPayload(raw);
        return e;
    }
}