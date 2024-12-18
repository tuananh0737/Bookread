package com.web.api;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.web.config.MessageException;
import com.web.dto.BookSearch;
import com.web.entity.Book;
import com.web.repository.AuthorRepository;
import com.web.repository.BookRepository;
import com.web.repository.GenresRepository;
import com.web.service.QrCodeService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class BookApi {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenresRepository genresRepository;

    private UserService userService;

    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/admin/add-update-book")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Book book) {
        if (book.getAuthor() == null) {
            throw new MessageException("Không được để trống tác giả");
        }
        if (book.getGenres() == null) {
            throw new MessageException("Không được để trống thể loại");
        }
        if (authorRepository.findById(book.getAuthor().getId()).isEmpty()) {
            throw new MessageException("Không tìm thấy tác giá");
        }
        if (genresRepository.findById(book.getGenres().getId()).isEmpty()) {
            throw new MessageException("Không tìm thấy thể loại");
        }

        try {
            byte[] qrCode = qrCodeService.generateQrCodeForBook(book);
            book.setQrCode(qrCode);
        } catch (WriterException | IOException e) {
            return new ResponseEntity<>("Failed to generate QR code", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Book result = bookRepository.save(book);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/public/find-all-book")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @DeleteMapping("/admin/delete-book")
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

    @PostMapping("/admin/generate-qr-codes-for-all-books")
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

    // API mới để tra cứu thông tin sách bằng mã QR
    @PostMapping("/public/find-book-by-qr")
    public ResponseEntity<?> findBookByQr(@RequestParam("qrCode") MultipartFile qrCode) {
        try {
            String decodedData = qrCodeService.decodeQr(qrCode.getBytes());
            if (decodedData != null && decodedData.startsWith("ID: ")) {
                Long bookId = Long.parseLong(decodedData.split(", ")[0].substring(4));
                Optional<Book> bookOptional = bookRepository.findById(bookId);
                if (bookOptional.isPresent()) {
                    return new ResponseEntity<>(bookOptional.get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Invalid QR code", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NotFoundException e) {
            return new ResponseEntity<>("Failed to decode QR code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
