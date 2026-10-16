package com.microservice.notification_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationEvent(
        String eventType,
        String email,
        String username,
        Map<String, Object> metadata
) {}
