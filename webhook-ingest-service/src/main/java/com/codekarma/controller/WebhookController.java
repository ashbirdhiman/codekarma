package com.codekarma.controller;

import com.codekarma.config.WebhookConfig;
import com.codekarma.dto.NormalizedEvent;
import com.codekarma.service.EventNormalizer;
import com.codekarma.service.KafkaProducerService;
import com.codekarma.service.SignatureValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/webhook")
public class WebhookController {


    private final SignatureValidator signatureValidator;
    private final WebhookConfig webhookConfig;
    private final ObjectMapper mapper = new ObjectMapper();
    private final EventNormalizer normalizer;
    private final KafkaProducerService producerService;


    public WebhookController(SignatureValidator signatureValidator, WebhookConfig webhookConfig, EventNormalizer normalizer, KafkaProducerService producerService) {
        this.signatureValidator = signatureValidator;
        this.webhookConfig = webhookConfig;
        this.normalizer = normalizer;
        this.producerService = producerService;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Event", required = false) String githubEvent,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestBody byte[] bodyBytes
    ) {
        try {
            if (!signatureValidator.isValid(webhookConfig.getSecret(), bodyBytes, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }


            if (!StringUtils.hasText(githubEvent)) {
                return ResponseEntity.badRequest().body("Missing X-GitHub-Event header");
            }


            JsonNode payload = mapper.readTree(new String(bodyBytes, StandardCharsets.UTF_8));


// Start with pull_request events only (per Week 1)
            if (!"pull_request".equals(githubEvent)) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Event ignored: " + githubEvent);
            }


            NormalizedEvent normalized = normalizer.normalize(githubEvent, payload);
            producerService.publish(normalized);


            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + e.getMessage());
        }
    }
}