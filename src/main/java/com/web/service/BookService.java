package com.web.service;

import com.google.zxing.WriterException;
import com.web.entity.Book;
import com.web.entity.Author;
import com.web.entity.Genres;
import com.web.repository.AuthorRepository;
import com.web.repository.BookRepository;
import com.web.repository.GenresRepository;
import com.web.config.MessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private QrCodeService qrCodeService;

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new MessageException("Sách không tồn tại", 404));
    }

    public Book saveOrUpdateBook(Book bookRequest) {
        if (bookRequest.getAuthor() == null || bookRequest.getAuthor().getId() == null) {
            throw new MessageException("Không được để trống tác giả");
        }
        if (bookRequest.getGenres() == null || bookRequest.getGenres().getId() == null) {
            throw new MessageException("Không được để trống thể loại");
        }

        Author author = new Author();
        author.setId(bookRequest.getAuthor().getId());

        Genres genres = new Genres();
        genres.setId(bookRequest.getGenres().getId());

        Book bookToSave;
        boolean isNewBook = (bookRequest.getId() == null);
        boolean isNameChanged = false;

        if (isNewBook) {
            bookToSave = bookRequest;
            bookToSave.setAuthor(author);
            bookToSave.setGenres(genres);
            bookToSave = bookRepository.save(bookToSave);
        } else {
            bookToSave = bookRepository.findById(bookRequest.getId())
                    .orElseThrow(() -> new MessageException("Không tìm thấy sách cần cập nhật"));

            if (bookRequest.getName() != null && !bookRequest.getName().equals(bookToSave.getName())) {
                isNameChanged = true;
            }

            bookToSave.setName(bookRequest.getName());
            bookToSave.setNumberPage(bookRequest.getNumberPage());
            bookToSave.setPublishYear(bookRequest.getPublishYear());
            bookToSave.setDescription(bookRequest.getDescription());
            bookToSave.setQuantity(bookRequest.getQuantity());
            bookToSave.setImage(bookRequest.getImage());
            bookToSave.setAuthor(author);
            bookToSave.setGenres(genres);
        }

        if (isNewBook || isNameChanged || bookToSave.getQrCode() == null) {
            try {
                byte[] qrCode = qrCodeService.generateQrCodeForBook(bookToSave);
                bookToSave.setQrCode(qrCode);
            } catch (WriterException | IOException e) {
                throw new MessageException("Lỗi khi tạo mã QR cho sách: " + e.getMessage());
            }
        }

        return bookRepository.save(bookToSave);
    }
}