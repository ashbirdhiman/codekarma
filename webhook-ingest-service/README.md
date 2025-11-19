# Webhook Ingest Service

## Week 1: GitHub Webhook Integration

This service receives and processes GitHub webhook events, normalizing them and publishing to Kafka for downstream processing.

## Features

### 🎯 **GitHub Webhook Integration**

- Receives GitHub webhook events via REST endpoint
- Validates webhook signatures (X-Hub-Signature-256)
- Processes pull request events (Week 1 focus)
- Graceful error handling and logging

### 🔄 **Event Normalization**

- Transforms GitHub webhook payloads into standardized events
- Extracts key information (user, repository, action, etc.)
- Generates unique event IDs for tracking

### 📤 **Kafka Publishing**

- Publishes normalized events to `events.ingest` topic
- Reliable message delivery with error handling
- JSON serialization of event data

### 🔒 **Security Features**

- GitHub webhook signature validation
- Configurable webhook secrets
- Request validation and sanitization

## API Endpoints

### POST `/webhook`

Receives GitHub webhook events.

**Headers:**

- `Content-Type: application/json`
- `X-GitHub-Event: pull_request` (supported event type)
- `X-GitHub-Delivery: <delivery-id>`
- `X-Hub-Signature-256: <signature>` (optional for testing)

**Response:**

- `200 OK` - Event processed successfully
- `400 Bad Request` - Invalid payload or missing headers
- `401 Unauthorized` - Invalid signature
- `202 Accepted` - Event ignored (unsupported type)

## Prerequisites

- Kafka running on `localhost:9092`
- ngrok or public endpoint for GitHub webhook delivery

## Quick Setup

### 1. Start Kafka (Docker)

```powershell
docker run -d --name kafka -p 9092:9092 apache/kafka:latest
```

### 2. Start the Service

```powershell
# From project root
.\mvnw.cmd -pl webhook-ingest-service -am spring-boot:run
```

### 3. Expose with ngrok

```powershell
ngrok http 8080
```

### 4. Configure GitHub Webhook

- **URL**: `https://your-ngrok-url.ngrok-free.app/webhook`
- **Content Type**: `application/json`
- **Secret**: `code-karma-secret`
- **Events**: Pull requests

## Configuration

### Application Settings (`application.yml`)

```yaml
server:
  port: 8080

webhook:
  secret: "code-karma-secret" # GitHub webhook secret

github:
  ingest:
    topic: events.ingest # Kafka topic for events

spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## Testing

### Local Testing (Without Signature)

```powershell
$headers = @{
  'Content-Type'='application/json'
  'X-GitHub-Event'='pull_request'
  'X-GitHub-Delivery'='test-123'
}
$body = '{"action":"opened","pull_request":{"number":1,"id":123},"repository":{"name":"test-repo"}}'
Invoke-RestMethod -Uri 'http://localhost:8080/webhook' -Method Post -Headers $headers -Body $body
```

### With ngrok

```powershell
Invoke-RestMethod -Uri 'https://your-ngrok-url.ngrok-free.app/webhook' -Method Post -Headers $headers -Body $body
```

## Event Flow

```
GitHub → Webhook Endpoint → Signature Validation → Event Normalization → Kafka (events.ingest)
```

## Supported Events

Currently supports **Pull Request** events:

- `opened` - New PR created
- `closed` - PR closed (merged or cancelled)
- `review_requested` - Review requested

Other event types are gracefully ignored with `202 Accepted` response.

## Logging

The service provides detailed logging:

- `🎯 Publishing event to topic 'events.ingest'` - Event being published
- `✅ Event published successfully with ID: <uuid>` - Successful publish
- `❌ Failed to publish event` - Publishing errors
- `⚠️ Event ignored: <event-type>` - Unsupported events

## Error Handling

- **Invalid Signature**: Returns `401 Unauthorized`
- **Missing Headers**: Returns `400 Bad Request`
- **Kafka Unavailable**: Logs error but doesn't fail webhook (graceful degradation)
- **Malformed JSON**: Returns `500 Internal Server Error`

## Security Notes

- Always use HTTPS endpoints in production
- Keep webhook secret secure and rotate regularly
- Monitor for suspicious webhook deliveries
- Validate all incoming data

## Architecture

The service follows Spring Boot best practices:

- **Controller**: `WebhookController` - HTTP endpoint handling
- **Service**: `EventNormalizer` - Event transformation logic
- **Service**: `KafkaProducerService` - Kafka publishing
- **Service**: `SignatureValidator` - Webhook security
- **Config**: `WebhookConfig` - Configuration management

## Next Steps

This service integrates with:

- **Event Scoring Service** - Consumes from `events.ingest`
- **Analytics Services** - Process normalized events
- **Notification Services** - React to specific events

## Troubleshooting

### Common Issues:

1. **Kafka Connection Failed**

   - Ensure Kafka is running on `localhost:9092`
   - Check Docker container: `docker logs kafka`

2. **Signature Validation Failed**

   - Verify webhook secret matches GitHub configuration
   - Check `X-Hub-Signature-256` header format

3. **ngrok Tunnel Issues**
   - Restart with correct port: `ngrok http 8080`
   - Check tunnel status: http://127.0.0.1:4040

### Health Check:

Service runs on http://localhost:8080 - you should see Spring Boot banner on successful startup.
