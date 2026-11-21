package com.microservice.notification_service.dto;

public record UserProfileUpdatedEvent(
        Long userId,
        String username,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePictureUrl
) {}