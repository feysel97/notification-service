package com.microservice.notification_service.service;

import com.microservice.notification_service.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {

    // Thread-safe map to hold active connections
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String email) {
        log.info("📱 New SSE subscription requested for email: {}", email); // Add this!

        SseEmitter emitter = new SseEmitter(60 * 1000L * 30);
        emitters.put(email, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE completed for {}", email);
            emitters.remove(email);
        });
        emitter.onTimeout(() -> {
            log.info("SSE timeout for {}", email);
            emitters.remove(email);
        });

        return emitter;
    }

    public void emitNotification(String email, NotificationResponse response) {
        SseEmitter emitter = emitters.get(email);
        // Add this! It tells us if the map found the active Flutter connection.
        log.info("🚀 Emitting notification to {}. Active connection found? {}", email, (emitter != null));

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(response));
            } catch (IOException e) {
                log.warn("Failed to send SSE to {}, removing emitter", email);
                emitters.remove(email);
            }
        }
    }
}