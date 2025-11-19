package com.codekarma.dto;

public class NormalizedEvent {
    private String eventId;
    private String eventType;
    private String action;
    private String repo;
    private String sender;
    private long timestamp; // epoch millis
    private String rawPayload; // optional: store raw JSON string or subset


    public NormalizedEvent() {}


    public NormalizedEvent(String eventId, String eventType, String action, String repo, String sender, long timestamp, String rawPayload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.action = action;
        this.repo = repo;
        this.sender = sender;
        this.timestamp = timestamp;
        this.rawPayload = rawPayload;
    }


// getters & setters


    public String getEventId() {
        return eventId;
    }


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getEventType() {
        return eventType;
    }


    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    public String getRepo() {
        return repo;
    }


    public void setRepo(String repo) {
        this.repo = repo;
    }


    public String getSender() {
        return sender;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(long epochMilli) {
        this.timestamp=epochMilli;

    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getRawPayload() {
        return rawPayload;
    }
}
