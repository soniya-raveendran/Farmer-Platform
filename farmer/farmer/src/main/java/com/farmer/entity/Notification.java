package com.farmer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String role;     // FARMER / RETAILER / ADMIN
    private Long userId;

    private boolean seen = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(String message, String role, Long userId) {
        this.message = message;
        this.role = role;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
    public boolean isSeen() { return seen; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSeen(boolean seen) { this.seen = seen; }
}
