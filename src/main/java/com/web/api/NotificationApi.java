package com.web.api;

import com.web.config.MessageException;
import com.web.entity.Notification;
import com.web.entity.User;
import com.web.repository.NotificationRepository;
import com.web.service.NotificationService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class NotificationApi {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/notifications")
    public ResponseEntity<?> getUserNotifications() {
        try {
            User user = userService.getUserWithAuthority();
            List<Notification> notifications = notificationRepository.findByUserId(user.getId());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể lấy thông báo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/send-notification")
    public ResponseEntity<?> sendNotificationToUser(@RequestParam Long userId, @RequestParam String content) {
        try {
            // Tìm kiếm người dùng theo ID
            User user = userService.findById(userId)
                    .orElseThrow(() -> new MessageException("Người dùng không tồn tại"));

            // Gửi thông báo bằng NotificationService
            notificationService.sendNotification(user, content);

            return new ResponseEntity<>("Thông báo đã được gửi thành công.", HttpStatus.OK);
        } catch (MessageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Có lỗi xảy ra khi gửi thông báo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
