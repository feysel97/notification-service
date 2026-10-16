package com.microservice.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank(message = "Recipient email is required")
        @Email(message = "Invalid email format")
        String recipientEmail,

        @NotBlank(message = "Subject is required")
        String subject,

        @NotBlank(message = "Message is required")
        String message,

        String eventType
) {}
