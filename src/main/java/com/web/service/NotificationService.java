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

        for (BorrowBook borrowBook : borrowBooks) {
            if (borrowBook.getReturned() != null && borrowBook.getReturned()) {
                continue; // Bỏ qua sách đã trả
            }

            Timestamp returnDueDate = borrowBook.getReturnDueDate();
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            long diff = returnDueDate.getTime() - currentTime.getTime();

            User user = borrowBook.getUser();
            if (diff <= 3 * 24 * 60 * 60 * 1000 && diff > 0) {
                // Gần đến hạn trả (3 ngày)
                sendNotification(user, "Sách " + borrowBook.getBook().getName() + " của bạn sắp đến hạn trả (3 ngày nữa).");
            } else if (diff < 0) {
                // Quá hạn trả
                sendNotification(user, "Sách " + borrowBook.getBook().getName() + " của bạn đã quá hạn trả. Vui lòng trả ngay!");
            }
        }
    }

    public void sendNotification(User user, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(content);
        notification.setCreatedDate(new Timestamp(System.currentTimeMillis()));
//        notification.setRead(false);
        notificationRepository.save(notification);
    }

}
