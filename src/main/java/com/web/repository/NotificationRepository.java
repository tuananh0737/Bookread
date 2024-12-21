package com.web.repository;

import com.web.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 ORDER BY n.createdDate DESC")
    List<Notification> findByUserId(Long userId);
}
