package com.web.api;

import com.web.config.MessageException;
import com.web.dto.BookSearch;
import com.web.entity.Book;
import com.web.repository.AuthorRepository;
import com.web.repository.BookRepository;
import com.web.repository.GenresRepository;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/admin/add-update-book")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Book book) {
        if(book.getAuthor() == null){
            throw new MessageException("Không được để trống tác giả");
        }
        if(book.getGenres() == null){
            throw new MessageException("Không được để trống thể loại");
        }
        if(authorRepository.findById(book.getAuthor().getId()).isEmpty()){
            throw new MessageException("Không tìm thấy tác giá");
        }
        if(genresRepository.findById(book.getGenres().getId()).isEmpty()){
            throw new MessageException("Không tìm thấy thể loại");
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
        if(param == null){
            param = "";
        }
        param = "%"+param+"%";
        List<Book> list = null;
        if(authorId == null && genreId == null){
            list = bookRepository.findByParam(param);
        }
        if(authorId == null && genreId != null){
            list = bookRepository.findByParamAndGenre(param,genreId);
        }
        if(authorId != null && genreId == null){
            list = bookRepository.findByParamAndAuthor(param,authorId);
        }
        if(authorId != null && genreId != null){
            list = bookRepository.findByParamAndAuthorAndGenre(param,authorId, genreId);
        }
        return list;
    }
}
