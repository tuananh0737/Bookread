package com.web.api;

import com.web.config.MessageException;
import com.web.entity.*;
import com.web.repository.BookRepository;
import com.web.repository.BorrowBookRepository;
import com.web.repository.BorrowRequestRepository;
import com.web.repository.UserRepository;
import com.web.service.NotificationService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @PostMapping("/system/add-borrowBook")
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

    @PostMapping("/system/return-book")
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

            // Notify users waiting for this book
            List<BorrowRequest> borrowRequests = borrowRequestRepository.findByBookIdAndNotifiedFalse(book.getId());
            for (BorrowRequest request : borrowRequests) {
                notificationService.sendNotification(request.getUser(),
                        "Sách \"" + book.getName() + "\" đã sẵn sàng để mượn.");
                request.setNotified(true);
                borrowRequestRepository.save(request);
            }

            return new ResponseEntity<>("Trả sách thành công", HttpStatus.OK);
        } catch (MessageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Có lỗi xảy ra khi trả sách: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Người dùng xem danh sách sách đã mượn
    @GetMapping("/user/find-borrowBook-by-user")
    public ResponseEntity<List<BorrowBook>> findBorrowBooksByUser() {
        User user = userService.getUserWithAuthority();
        List<BorrowBook> borrowBooks = borrowBookRepository.findByUser(user.getId());
        return new ResponseEntity<>(borrowBooks, HttpStatus.OK);
    }

    // Admin xem danh sách mượn của người dùng
    @GetMapping("/system/find-borrowBook")
    public ResponseEntity<List<BorrowBook>> findBorrowBooksByUserId(@RequestParam("userId") Long userId) {
        List<BorrowBook> borrowBooks = borrowBookRepository.findByUser(userId);
        return new ResponseEntity<>(borrowBooks, HttpStatus.OK);
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

    @GetMapping("/admin/statistics-monthly")
    public ResponseEntity<?> getMonthlyStatistics(@RequestParam("month") int month, @RequestParam("year") int year) {
        try {
            long totalBorrowed = borrowBookRepository.countTotalBorrowedByMonth(month, year);
            long returnedOnTime = borrowBookRepository.countReturnedOnTimeByMonth(month, year);
            long returnedLate = borrowBookRepository.countReturnedLateByMonth(month, year);
            long notReturned = borrowBookRepository.countNotReturnedByMonth(month, year);

            return new ResponseEntity<>(String.format(
                    "Thống kê tháng %d/%d: Tổng sách mượn: %d, Trả đúng hạn: %d, Trả quá hạn: %d, Chưa trả: %d",
                    month, year, totalBorrowed, returnedOnTime, returnedLate, notReturned
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể thực hiện thống kê: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/statistics-range")
    public ResponseEntity<?> getStatisticsByRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
            Timestamp end = Timestamp.valueOf(endDate.atTime(23, 59, 59));

            long totalBorrowed = borrowBookRepository.countTotalBorrowedBetween(start, end);

            return new ResponseEntity<>(String.format(
                    "Thống kê từ %s đến %s: Tổng sách mượn: %d",
                    startDate, endDate, totalBorrowed
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể thực hiện thống kê: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/statistics-average-borrow-time")
    public ResponseEntity<?> getAverageBorrowTime() {
        try {
            Double averageTime = borrowBookRepository.calculateAverageBorrowTime();
            return new ResponseEntity<>(String.format("Thời gian mượn trung bình: %.2f ngày", averageTime), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể tính thời gian mượn trung bình: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/general-statistics")
    public ResponseEntity<?> getGeneralStatistics() {
        try {
            long totalAvailableBooks = bookRepository.countTotalAvailableBooks();
            long booksCurrentlyBorrowed = borrowBookRepository.countBooksCurrentlyBorrowed();
            long totalRegisteredUsers = userRepository.countTotalRegisteredUsers();

            return new ResponseEntity<>(String.format(
                    "Sách hiện có: %d, Sách đã cho mượn: %d, Thành viên đăng ký: %d",
                    totalAvailableBooks, booksCurrentlyBorrowed, totalRegisteredUsers
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể lấy thống kê: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/dashboard-statistics")
    public ResponseEntity<?> getDashboardStatistics() {
        try {
            long totalAvailableBooks = bookRepository.countTotalAvailableBooks();
            long booksCurrentlyBorrowed = borrowBookRepository.countBooksCurrentlyBorrowed();
            long totalRegisteredUsers = userRepository.countTotalRegisteredUsers();
            long totalBorrowedBooks = borrowBookRepository.countTotalBorrowed();
            long returnedOnTime = borrowBookRepository.countReturnedOnTime();
            long returnedLate = borrowBookRepository.countReturnedLate();
            long notReturned = borrowBookRepository.countNotReturned();

            // Top sách được mượn nhiều nhất
            List<Object[]> topBooks = borrowBookRepository.findTopBooks();

            // Top người dùng mượn nhiều nhất
            List<Object[]> topUsers = borrowBookRepository.findTopUsers();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalAvailableBooks", totalAvailableBooks);
            statistics.put("booksCurrentlyBorrowed", booksCurrentlyBorrowed);
            statistics.put("totalRegisteredUsers", totalRegisteredUsers);
            statistics.put("totalBorrowedBooks", totalBorrowedBooks);
            statistics.put("returnedOnTime", returnedOnTime);
            statistics.put("returnedLate", returnedLate);
            statistics.put("notReturned", notReturned);
            statistics.put("topBooks", topBooks);
            statistics.put("topUsers", topUsers);

            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Không thể lấy thống kê: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
