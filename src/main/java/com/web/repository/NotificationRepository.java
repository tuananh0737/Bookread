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

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user.id = ?1 AND n.bookId = ?2 AND n.content LIKE ?3")
    boolean existsNotificationForBook(Long userId, Long bookId, String content);

    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 AND n.bookId = ?2 AND n.content LIKE ?3 ORDER BY n.createdDate DESC")
    List<Notification> findRecentNotifications(Long userId, Long bookId, String content);

}
