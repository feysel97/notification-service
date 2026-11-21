package com.microservice.notification_service.listener;

import com.microservice.notification_service.dto.UserProfileUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfileUpdateListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.profile.update.queue", durable = "true"),
            exchange = @Exchange(value = "user-exchange", type = "topic"),
            key = "user.profile.updated"
    ))
    public void handleProfileUpdate(UserProfileUpdatedEvent event) {
        log.info("Notification-Service caught PROFILE_UPDATED event for user: {}", event.username());
        log.info("-> New Name: {} {}", event.firstName(), event.lastName());

        // In the future, this could trigger a "Your account details were changed" security email
    }
}
