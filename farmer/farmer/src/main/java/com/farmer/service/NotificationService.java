package com.farmer.service;

import com.farmer.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface NotificationService {

    void notify(String message, String role, Long userId);

    List<Notification> getUnreadNotifications(Long userId, String role);

    void markAsRead(Long id);

    SseEmitter subscribe(Long userId, String role);
}
