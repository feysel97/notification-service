package com.microservice.notification_service.dto;

import com.microservice.notification_service.enums.NotificationStatus;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String recipientEmail,
        String subject,
        String message,
        NotificationStatus status,
        String eventType,
        String errorMessage,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {}
