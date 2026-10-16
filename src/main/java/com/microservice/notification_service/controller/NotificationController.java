package com.microservice.notification_service.controller;

import com.microservice.notification_service.dto.NotificationRequest;
import com.microservice.notification_service.dto.NotificationResponse;
import com.microservice.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Secured endpoint for end-users
    @GetMapping("/my-notifications")
    public List<NotificationResponse> getMyNotifications(@RequestHeader("X-User-Email") String userEmail) {
        return notificationService.getUserNotifications(userEmail);
    }

    // --- ADMIN ENDPOINTS BELOW ---

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse createNotification(
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles,
            @Valid @RequestBody NotificationRequest request) {

        requireAdmin(roles);
        return notificationService.createNotification(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getNotification(
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles,
            @PathVariable Long id) {

        requireAdmin(roles);
        return notificationService.getNotificationById(id);
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications(
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {

        requireAdmin(roles);
        return notificationService.getAllNotifications();
    }

    // Helper method to enforce admin access
    private void requireAdmin(String roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Requires ADMIN role");
        }
    }
}