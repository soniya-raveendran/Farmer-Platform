package com.farmer.repository;

import com.farmer.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndRoleAndSeenFalse(Long userId, String role);

    List<Notification> findByUserIdAndRoleOrderByCreatedAtDesc(Long userId, String role);
}
