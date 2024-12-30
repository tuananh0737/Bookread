package com.web.service;

import com.web.entity.BorrowRequest;
import com.web.entity.BorrowBook;
import com.web.entity.User;
import com.web.entity.Book;
import com.web.repository.BorrowRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BorrowRequestService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private NotificationService notificationService;

    public void saveBorrowRequest(User user, Book book) {
        BorrowRequest request = new BorrowRequest();
        request.setUser(user);
        request.setBook(book);
        request.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        borrowRequestRepository.save(request);
    }

    public void notifyUsersWhenBookAvailable(Book book) {
        List<BorrowRequest> requests = borrowRequestRepository.findByBookIdAndNotifiedFalse(book.getId());
        for (BorrowRequest request : requests) {
            notificationService.sendNotification(request.getUser(),
                    "The book \"" + book.getName() + "\" is now available for borrowing.");
            request.setNotified(true);
            borrowRequestRepository.save(request);
        }
    }
}
