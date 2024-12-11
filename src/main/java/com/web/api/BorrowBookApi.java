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

    @PostMapping("/admin/add-borrowBook")
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

        BorrowBook savedBorrowBook = borrowBookRepository.save(borrowBook);
        return new ResponseEntity<>(savedBorrowBook, HttpStatus.CREATED);
    }



    @GetMapping("/user/find-borrowBook-by-user")
    public ResponseEntity<List<BorrowBook>> findBorrowBooksByUser() {
        User user = userService.getUserWithAuthority();
        List<BorrowBook> borrowBooks = borrowBookRepository.findByUser(user.getId());
        return new ResponseEntity<>(borrowBooks, HttpStatus.OK);
    }

    @GetMapping("/admin/find-borrowBook")
    public ResponseEntity<List<BorrowBook>> findBorrowBooksByUserId(@RequestParam("userId") Long userId) {
        List<BorrowBook> borrowBooks = borrowBookRepository.findByUser(userId);
        return new ResponseEntity<>(borrowBooks, HttpStatus.OK);
    }


    @DeleteMapping("/admin/delete-borrowBook")
    public ResponseEntity<?> adminDeleteBorrowBook(@RequestParam("id") Long borrowBookId) {
        BorrowBook borrowBook = borrowBookRepository.findById(borrowBookId)
                .orElseThrow(() -> new MessageException("Không tìm thấy thông tin mượn sách"));

        Book book = borrowBook.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        borrowBookRepository.delete(borrowBook);
        return new ResponseEntity<>("Xóa thông tin mượn sách thành công", HttpStatus.OK);
    }
}
