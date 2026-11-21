package com.microservice.notification_service.controller;

import com.microservice.notification_service.dto.NotificationRequest;
import com.microservice.notification_service.dto.NotificationResponse;
import com.microservice.notification_service.service.NotificationService;
import com.microservice.notification_service.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseService sseService;

    // Your existing route
    @GetMapping("/my-notifications")
    public List<NotificationResponse> getMyNotifications(@RequestHeader("X-User-Email") String userEmail) {
        return notificationService.getUserNotifications(userEmail);
    }

    // Our new SSE route
    @GetMapping(value = "/sse/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestHeader(value = "X-User-Email", required = false) String userEmail) {
        log.info("📥 Connection reached /sse/stream! X-User-Email header is: {}", userEmail);

        if (userEmail == null || userEmail.isBlank()) {
            log.error("🚨 Rejecting SSE connection: X-User-Email is null or empty!");
            return null;
        }
        return sseService.subscribe(userEmail);
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