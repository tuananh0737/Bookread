package com.web.service;

import com.web.entity.BorrowBook;
import com.web.entity.Notification;
import com.web.entity.User;
import com.web.repository.BorrowBookRepository;
import com.web.repository.NotificationRepository;
import com.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Tự động chạy mỗi ngày lúc 00:00
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndSendNotifications() {
        List<BorrowBook> borrowBooks = borrowBookRepository.findAll();
        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

        for (BorrowBook borrowBook : borrowBooks) {
            if (borrowBook.getReturned() != null && borrowBook.getReturned()) {
                continue; // Bỏ qua sách đã trả
            }

            Timestamp returnDueDate = borrowBook.getReturnDueDate();
            long diff = returnDueDate.getTime() - currentTime.getTime();
            User user = borrowBook.getUser();
            Long bookId = borrowBook.getBook().getId();
            String content;

            if (diff <= 3 * 24 * 60 * 60 * 1000 && diff > 0) {
                content = "Sách " + borrowBook.getBook().getName() + " của bạn sắp đến hạn trả (3 ngày nữa).";
            } else if (diff < 0) {
                content = "Sách " + borrowBook.getBook().getName() + " của bạn đã quá hạn trả. Vui lòng trả ngay!";
            } else {
                continue;
            }

            // Gửi thông báo mới kèm theo bookId
            sendNotification(user, content, bookId);
        }
    }

    public void sendNotification(User user, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(content);
        notification.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
    }

    //chủ động gửi thông báo
    public int sendOverdueNotificationsToAllUsers() {
        List<BorrowBook> borrowBooks = borrowBookRepository.findAll();
        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
        int notificationCount = 0;

        for (BorrowBook borrowBook : borrowBooks) {
            if (borrowBook.getReturned() != null && borrowBook.getReturned()) {
                continue;
            }

            Timestamp returnDueDate = borrowBook.getReturnDueDate();
            long diff = returnDueDate.getTime() - currentTime.getTime();
            User user = borrowBook.getUser();
            String content;

            if (diff <= 3 * 24 * 60 * 60 * 1000 && diff > 0) {
                content = "Sách " + borrowBook.getBook().getName() + " của bạn sắp đến hạn trả (3 ngày nữa).";
            } else if (diff < 0) {
                content = "Sách " + borrowBook.getBook().getName() + " của bạn đã quá hạn trả. Vui lòng trả ngay!";
            } else {
                continue;
            }

            List<Notification> recentNotifications = notificationRepository.findRecentNotifications(
                    user.getId(), borrowBook.getBook().getId(), content);

            if (!recentNotifications.isEmpty()) {
                Notification lastNotification = recentNotifications.get(0);
                long timeSinceLastNotification = currentTime.getTime() - lastNotification.getCreatedDate().getTime();
                if (timeSinceLastNotification < 2 * 24 * 60 * 60 * 1000) {
                    continue;
                }
            }

            // Gửi thông báo mới
            sendNotification(user, content, borrowBook.getBook().getId());
            notificationCount++;
        }

        return notificationCount;
    }

    public void sendNotification(User user, String content, Long bookId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(content);
        notification.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        notification.setBookId(bookId);
        notificationRepository.save(notification);
    }
}
