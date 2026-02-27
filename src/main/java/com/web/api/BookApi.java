package com.web.api;

import com.google.zxing.WriterException;
import com.web.config.MessageException;
import com.web.dto.BookSearch;
import com.web.entity.Book;
import com.web.entity.BookLocation;
import com.web.repository.AuthorRepository;
import com.web.repository.BookLocationRepository;
import com.web.repository.BookRepository;
import com.web.repository.GenresRepository;
import com.web.service.BookService;
import com.web.service.QrCodeService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookApi {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private BookService bookService;

    private UserService userService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private BookLocationRepository bookLocationRepository;

    // Đã đổi từ /admin/ sang /system/ để cả Admin và Librarian đều dùng được
    @PostMapping("/system/add-update-book")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Book book) {
        Book result = bookService.saveOrUpdateBook(book);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/public/find-all-book")
    public ResponseEntity<Page<Book>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return new ResponseEntity<>(bookPage, HttpStatus.OK);
    }

    // Đã đổi từ /admin/ sang /system/
    @DeleteMapping("/system/delete-book")
    public void delete(@RequestParam("id") Long id) {
        bookRepository.deleteById(id);
    }

    @PostMapping("/public/search-book")
    public List<Book> search(@RequestBody BookSearch bookSearch) {
        String param = bookSearch.getParam();
        Long authorId = bookSearch.getAuthorId();
        Long genreId = bookSearch.getGenreId();
        if (param == null) {
            param = "";
        }
        param = "%" + param + "%";
        List<Book> list = null;
        if (authorId == null && genreId == null) {
            list = bookRepository.findByParam(param);
        }
        if (authorId == null && genreId != null) {
            list = bookRepository.findByParamAndGenre(param, genreId);
        }
        if (authorId != null && genreId == null) {
            list = bookRepository.findByParamAndAuthor(param, authorId);
        }
        if (authorId != null && genreId != null) {
            list = bookRepository.findByParamAndAuthorAndGenre(param, authorId, genreId);
        }
        return list;
    }

    // Đã đổi từ /admin/ sang /system/
    @PostMapping("/system/generate-qr-codes-for-all-books")
    public ResponseEntity<?> generateQrCodesForAllBooks() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            try {
                byte[] qrCode = qrCodeService.generateQrCodeForBook(book);
                book.setQrCode(qrCode);
                bookRepository.save(book);
            } catch (WriterException | IOException e) {
                return new ResponseEntity<>("Failed to generate QR code for book ID: " + book.getId(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("QR codes generated and saved for all books.", HttpStatus.OK);
    }

    @PostMapping("/public/find-book-by-qr")
    public ResponseEntity<?> findBookByQr(@RequestBody String qrData) {
        try {
            if (qrData != null && qrData.startsWith("ID: ")) {
                Long bookId = Long.parseLong(qrData.split(", ")[0].substring(4));
                Optional<Book> bookOptional = bookRepository.findById(bookId);
                if (bookOptional.isPresent()) {
                    return new ResponseEntity<>(bookOptional.get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Invalid QR code", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process QR code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Đã đổi từ /admin/ sang /system/
    @PostMapping("/system/add-or-update-location")
    public ResponseEntity<?> addOrUpdateLocation(@RequestParam("bookId") Long bookId,
                                                 @RequestBody BookLocation location) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new MessageException("Sách không tồn tại"));

        if (book.getLocation() != null) {
            location.setId(book.getLocation().getId());
        }

        BookLocation savedLocation = bookLocationRepository.save(location);
        book.setLocation(savedLocation);
        bookRepository.save(book);

        return new ResponseEntity<>("Cập nhật vị trí sách thành công", HttpStatus.OK);
    }

    @GetMapping("/public/location-book")
    public ResponseEntity<?> getBookLocation(@RequestParam("bookId") Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new MessageException("Sách không tồn tại"));

        BookLocation location = book.getLocation();
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @GetMapping("/public/book-by-location")
    public ResponseEntity<List<Book>> getBooksByLocation(@RequestParam("locationId") Long locationId) {
        List<Book> books = bookRepository.findByBookLocation(locationId);
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/public/books-by-rating")
    public ResponseEntity<List<Book>> getBooksByRating() {
        List<Book> books = bookRepository.findAllOrderByAverageRatingDesc();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }
}