package com.microservice.notification_service.messaging;

import com.microservice.notification_service.dto.NotificationRequest;
import com.microservice.notification_service.event.NotificationEvent;
import com.microservice.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${app.rabbitmq.notification-queue}")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received {} event from RabbitMQ for email: {}", event.eventType(), event.email());

        String displayName = (event.username() != null && !event.username().isBlank())
                ? event.username()
                : "User";

        String subject;
        String message;

        switch (event.eventType()) {
            case "USER_REGISTERED" -> {
                subject = "Welcome to Our Platform!";
                message = String.format("Hi %s,\n\nYour account has been successfully created. Welcome aboard!", displayName);
            }
            case "PASSWORD_RESET_REQUESTED" -> {
                subject = "Password Reset Request";
                String resetLink = (String) event.metadata().getOrDefault("resetLink", "URL_MISSING");
                message = String.format("Hi %s,\n\nYou requested a password reset. Click the link below to reset your password:\n%s", displayName, resetLink);
            }
            case "PASSWORD_CHANGED" -> {
                subject = "Password Successfully Changed";
                message = String.format("Hi %s,\n\nYour password has been successfully updated. If this was not you, please contact support immediately.", displayName);
            }
            case "ACCOUNT_DEACTIVATED" -> {
                subject = "Account Deactivated";
                message = String.format("Hi %s,\n\nYour account has been deactivated. If you wish to reactivate, please contact our support team.", displayName);
            }
            default -> {
                log.warn("Ignored unsupported event type: {}", event.eventType());
                return;
            }
        }

        NotificationRequest request = new NotificationRequest(
                event.email(),
                subject,
                message,
                event.eventType()
        );

        notificationService.createNotification(request);
    }
}