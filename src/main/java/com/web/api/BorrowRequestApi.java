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
@CrossOrigin("*")
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

    @PostMapping("/admin/borrow-requests")
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

    @PostMapping("/system/return-borrow")
    public ResponseEntity<String> returnBook(@RequestParam Long borrowBookId) {
        BorrowBook borrowBook = borrowBookRepository.findById(borrowBookId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        borrowBook.setReturned(true);
        borrowBook.setActualReturnDate(new Timestamp(System.currentTimeMillis()));

        Book book = borrowBook.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        borrowRequestService.notifyUsersWhenBookAvailable(book);
        return ResponseEntity.ok("Book returned and notifications sent.");
    }


}