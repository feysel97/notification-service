package com.microservice.notification_service.service;

import com.microservice.notification_service.dto.NotificationRequest;
import com.microservice.notification_service.dto.NotificationResponse;
import com.microservice.notification_service.entity.Notification;
import com.microservice.notification_service.enums.NotificationStatus;
import com.microservice.notification_service.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProcessor notificationProcessor;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientEmail(request.recipientEmail())
                .subject(request.subject())
                .message(request.message())
                .eventType(request.eventType())
                .status(NotificationStatus.PENDING)
                .build();

        // 1. Save to database as PENDING
        Notification saved = notificationRepository.save(notification);

        // 2. Fire and forget (returns void, runs on background thread)
        notificationProcessor.processAndSend(saved);

        // 3. Return the PENDING response instantly without waiting
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        return mapToResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String email) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientEmail(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getEventType(),
                notification.getErrorMessage(),
                notification.getSentAt(),
                notification.getCreatedAt()
        );
    }
}