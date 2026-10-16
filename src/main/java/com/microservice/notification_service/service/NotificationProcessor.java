package com.microservice.notification_service.service;

import com.microservice.notification_service.entity.Notification;
import com.microservice.notification_service.enums.NotificationStatus;
import com.microservice.notification_service.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProcessor {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Async
    public void processAndSend(Notification notification) {
        try {
            emailService.sendEmail(
                    notification.getRecipientEmail(),
                    notification.getSubject(),
                    notification.getMessage()
            );
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(java.time.LocalDateTime.now());
            notification.setErrorMessage(null);
        } catch (Exception ex) {
            log.error("Failed to send notification ID {} to {}: {}",
                    notification.getId(), notification.getRecipientEmail(), ex.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
        }
        // Save the updated status on the background thread
        notificationRepository.save(notification);
    }
}