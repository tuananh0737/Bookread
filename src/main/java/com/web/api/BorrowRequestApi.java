package com.web.api;

import com.web.entity.*;
import com.web.repository.BookRepository;
import com.web.repository.BorrowBookRepository;
import com.web.repository.UserRepository;
import com.web.service.BorrowRequestService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api")
public class BorrowRequestApi {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRequestService borrowRequestService;

    @PostMapping("/system/borrow-requests")
    public ResponseEntity<String> saveBorrowRequest(@RequestParam Long userId, @RequestParam Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getQuantity() <= 0) {
            borrowRequestService.saveBorrowRequest(user, book);
            return ResponseEntity.ok("Borrow request saved. You will be notified when the book is available.");
        } else {
            return ResponseEntity.badRequest().body("Sách" + book.getName() + "đã sẵn sàng");
        }
    }

    @Autowired
    private com.web.service.CirculationService circulationService;

    /** Legacy path retained for existing clients; all returns use the same transaction. */
    @PostMapping("/system/return-borrow")
    public ResponseEntity<String> returnBook(@RequestParam Long borrowBookId) {
        circulationService.returnLoan(borrowBookId);
        return ResponseEntity.ok("Book returned");
    }
}
