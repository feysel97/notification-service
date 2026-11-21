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
    // 1. Inject the new SseService
    private final SseService sseService;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientEmail(request.recipientEmail())
                .subject(request.subject())
                .message(request.message())
                .eventType(request.eventType())
                .status(NotificationStatus.PENDING)
                .build();

        Notification saved = notificationRepository.save(notification);
        notificationProcessor.processAndSend(saved);

        NotificationResponse response = mapToResponse(saved);

        // 2. Fire the real-time event to Flutter!
        sseService.emitNotification(request.recipientEmail(), response);

        return response;
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