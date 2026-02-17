package com.farmer.controller;

import com.farmer.entity.Notification;
import com.farmer.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/stream/{userId}")
    public SseEmitter stream(@PathVariable Long userId, @RequestParam String role) {
        return notificationService.subscribe(userId, role);
    }

    @GetMapping("/{userId}")
    public List<Notification> getUnread(@PathVariable Long userId, @RequestParam String role) {
        return notificationService.getUnreadNotifications(userId, role);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
