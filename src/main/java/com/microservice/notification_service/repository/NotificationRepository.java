package com.microservice.notification_service.repository;

import com.microservice.notification_service.entity.Notification;
import com.microservice.notification_service.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
    // Add this method to find failed notifications
    List<Notification> findByStatus(NotificationStatus status);
}
