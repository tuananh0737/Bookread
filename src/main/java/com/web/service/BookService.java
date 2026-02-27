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
        // 1. Validate dữ liệu đầu vào
        if (bookRequest.getAuthor() == null || bookRequest.getAuthor().getId() == null) {
            throw new MessageException("Không được để trống tác giả");
        }
        if (bookRequest.getGenres() == null || bookRequest.getGenres().getId() == null) {
            throw new MessageException("Không được để trống thể loại");
        }

        Author author = authorRepository.findById(bookRequest.getAuthor().getId())
                .orElseThrow(() -> new MessageException("Không tìm thấy tác giả"));
        Genres genres = genresRepository.findById(bookRequest.getGenres().getId())
                .orElseThrow(() -> new MessageException("Không tìm thấy thể loại"));

        Book bookToSave;

        // 2. Phân loại xử lý: Thêm mới (id = null) hay Cập nhật (id != null)
        if (bookRequest.getId() == null) {
            // LÀ THÊM MỚI
            bookToSave = bookRequest;
            bookToSave.setAuthor(author);
            bookToSave.setGenres(genres);
            // Lưu lần 1 để database sinh ra ID
            bookToSave = bookRepository.save(bookToSave);
        } else {
            // LÀ CẬP NHẬT
            // Lấy sách cũ từ DB lên để không bị mất các trường như comments, location, averageRating
            bookToSave = bookRepository.findById(bookRequest.getId())
                    .orElseThrow(() -> new MessageException("Không tìm thấy sách cần cập nhật"));

            // Chỉ cập nhật các trường được phép thay đổi
            bookToSave.setName(bookRequest.getName());
            bookToSave.setNumberPage(bookRequest.getNumberPage());
            bookToSave.setPublishYear(bookRequest.getPublishYear());
            bookToSave.setDescription(bookRequest.getDescription());
            bookToSave.setQuantity(bookRequest.getQuantity());
            bookToSave.setImage(bookRequest.getImage());
            bookToSave.setAuthor(author);
            bookToSave.setGenres(genres);
            // Không set lại comments, location hay rating ở đây để bảo toàn dữ liệu
        }

        // 3. Xử lý tạo lại/tạo mới QR Code (sau khi chắc chắn sách đã có ID)
        try {
            byte[] qrCode = qrCodeService.generateQrCodeForBook(bookToSave);
            bookToSave.setQrCode(qrCode);
        } catch (WriterException | IOException e) {
            throw new MessageException("Lỗi khi tạo mã QR cho sách: " + e.getMessage());
        }

        // 4. Lưu kết quả cuối cùng
        return bookRepository.save(bookToSave);
    }
}