package com.microservice.notification_service.scheduler;

import com.microservice.notification_service.entity.Notification;
import com.microservice.notification_service.enums.NotificationStatus;
import com.microservice.notification_service.repository.NotificationRepository;
import com.microservice.notification_service.service.NotificationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationProcessor notificationProcessor;

    // Runs every 5 minutes
    @Scheduled(fixedDelay = 300000)
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);

        if (failedNotifications.isEmpty()) {
            return;
        }

        log.info("Found {} failed notifications. Attempting to resend...", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            log.info("Retrying notification ID: {}", notification.getId());
            notificationProcessor.processAndSend(notification);
        }
    }
}
