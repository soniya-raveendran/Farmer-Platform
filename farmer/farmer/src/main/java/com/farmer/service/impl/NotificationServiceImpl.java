package com.farmer.service.impl;

import com.farmer.entity.Notification;
import com.farmer.repository.NotificationRepository;
import com.farmer.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationServiceImpl(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public void notify(String message, String role, Long userId) {
        Notification note = new Notification(message, role, userId);
        Notification saved = notificationRepo.save(note);

        String key = userId + "_" + role.toUpperCase();
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(saved));
            } catch (IOException e) {
                emitters.remove(key);
            }
        }
    }

    public SseEmitter subscribe(Long userId, String role) {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L); // 24h timeout
        String key = userId + "_" + role.toUpperCase();

        emitters.put(key, emitter);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() -> emitters.remove(key));
        emitter.onError((e) -> emitters.remove(key));

        return emitter;
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId, String role) {
        return notificationRepo.findByUserIdAndRoleAndSeenFalse(userId, role);
    }

    @Override
    public void markAsRead(Long id) {
        Notification note = notificationRepo.findById(id).orElseThrow();
        note.setSeen(true);
        notificationRepo.save(note);
    }
}
