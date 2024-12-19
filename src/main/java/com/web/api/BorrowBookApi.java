package com.web.api;

import com.web.config.MessageException;
import com.web.entity.*;
import com.web.repository.BookRepository;
import com.web.repository.BorrowBookRepository;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class BorrowBookApi {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/librarian/add-borrowBook")
    public ResponseEntity<?> addBorrowBook(@RequestBody BorrowBook borrowBook) {
        if (borrowBook.getUser() == null || borrowBook.getUser().getId() == null) {
            throw new MessageException("Thông tin người dùng không hợp lệ");
        }
        User user = userService.findById(borrowBook.getUser().getId())
                .orElseThrow(() -> new MessageException("Người dùng không tồn tại"));

        if (borrowBook.getBook() == null || borrowBook.getBook().getId() == null) {
            throw new MessageException("Thông tin sách không hợp lệ");
        }
        Book book = bookRepository.findById(borrowBook.getBook().getId())
                .orElseThrow(() -> new MessageException("Sách không tồn tại"));

        if (book.getQuantity() <= 0) {
            throw new MessageException("Sách đã hết số lượng");
        }

        if (borrowBookRepository.findByUserAndBook(user.getId(), book.getId()).isPresent()) {
            throw new MessageException("Người dùng đã mượn sách này");
        }

        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        borrowBook.setUser(user);
        borrowBook.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        borrowBook.setReturnDueDate(Timestamp.valueOf(LocalDateTime.now().plusDays(10))); // Thêm hạn trả sách
        borrowBook.setReturned(false); // Mặc định chưa trả

        BorrowBook savedBorrowBook = borrowBookRepository.save(borrowBook);
        return new ResponseEntity<>(savedBorrowBook, HttpStatus.CREATED);
    }

    @PostMapping("/librarian/return-book")
    public ResponseEntity<?> returnBook(@RequestParam("borrowBookId") Long borrowBookId) {
        try {
            BorrowBook borrowBook = borrowBookRepository.findById(borrowBookId)
                    .orElseThrow(() -> new MessageException("Thông tin mượn sách không tồn tại"));

            if (borrowBook.getActualReturnDate() != null && borrowBook.getReturned() != null && borrowBook.getReturned()) {
                return new ResponseEntity<>("Sách đã được trả trước đó", HttpStatus.BAD_REQUEST);
            }

            Book book = borrowBook.getBook();
            book.setQuantity(book.getQuantity() + 1);
            bookRepository.save(book);

            borrowBook.setActualReturnDate(new Timestamp(System.currentTimeMillis()));
            borrowBook.setReturned(true);
            borrowBookRepository.save(borrowBook);

            return new ResponseEntity<>("Trả sách thành công", HttpStatus.OK);
        } catch (MessageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Có lỗi xảy ra khi trả sách: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/admin/statistics")
    public ResponseEntity<?> getLibraryStatistics() {
        long totalBorrowed = borrowBookRepository.countTotalBorrowed();
        long returnedOnTime = borrowBookRepository.countReturnedOnTime();
        long returnedLate = borrowBookRepository.countReturnedLate();
        long notReturned = borrowBookRepository.countNotReturned();

        return new ResponseEntity<>(String.format(
                "Tổng sách mượn: %d, Trả đúng hạn: %d, Trả quá hạn: %d, Chưa trả: %d",
                totalBorrowed, returnedOnTime, returnedLate, notReturned
        ), HttpStatus.OK);
    }

    @GetMapping("/admin/statistics-by-user")
    public ResponseEntity<?> getUserStatistics(@RequestParam("userId") Long userId) {
        try {
            long totalBorrowed = borrowBookRepository.countByUser(userId);
            long returnedOnTime = borrowBookRepository.countByUserAndReturnedOnTime(userId);
            long returnedLate = borrowBookRepository.countByUserAndReturnedLate(userId);
            long notReturned = borrowBookRepository.countByUserAndNotReturned(userId);

            return new ResponseEntity<>(String.format(
                    "Thống kê cho người dùng (ID: %d): Tổng sách mượn: %d, Trả đúng hạn: %d, Trả quá hạn: %d, Chưa trả: %d",
                    userId, totalBorrowed, returnedOnTime, returnedLate, notReturned
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể thực hiện thống kê: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
