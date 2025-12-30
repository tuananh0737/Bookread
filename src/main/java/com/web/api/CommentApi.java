package com.web.api;

import com.web.config.MessageException;
import com.web.dto.CommentDto;
import com.web.entity.Book;
import com.web.entity.Comment;
import com.web.entity.User;
import com.web.repository.BookRepository;
import com.web.repository.CommentRepository;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentApi {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/user/add-comment")
    public ResponseEntity<?> save(@RequestBody Comment comment) {
        User user = userService.getUserWithAuthority();
        comment.setUser(user);
        comment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        Comment result = commentRepository.save(comment);

        Float averageStar = commentRepository.findAverageStarByBookId(comment.getBook().getId());
        Book book = bookRepository.findById(comment.getBook().getId())
                .orElseThrow(() -> new MessageException("Sách không tồn tại"));
        book.setAverageRating(averageStar);
        bookRepository.save(book);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


    @DeleteMapping("/user/delete-comment")
    public void delete(@RequestParam("id") Long id) {
        User user = userService.getUserWithAuthority();
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new MessageException("Bình luận không tồn tại"));
        if (comment.getUser().getId() != user.getId()) {
            throw new MessageException("Bạn không đủ quyền");
        }
        commentRepository.deleteById(id);

        Float averageStar = commentRepository.findAverageStarByBookId(comment.getBook().getId());
        Book book = bookRepository.findById(comment.getBook().getId()).orElseThrow(() -> new MessageException("Sách không tồn tại"));
        book.setAverageRating(averageStar);
        bookRepository.save(book);
    }


    @DeleteMapping("/admin/delete-comment")
    public void deleteByAdmin(@RequestParam("id") Long id) {
        commentRepository.deleteById(id);

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new MessageException("Bình luận không tồn tại"));
        Float averageStar = commentRepository.findAverageStarByBookId(comment.getBook().getId());
        Book book = bookRepository.findById(comment.getBook().getId()).orElseThrow(() -> new MessageException("Sách không tồn tại"));
        book.setAverageRating(averageStar);
        bookRepository.save(book);
    }

    @GetMapping("/public/find-comment-book")
    public List<Comment> findByBook(@RequestParam("bookId") Long bookId) {
        return commentRepository.findByBook(bookId);
    }

    @GetMapping("/user/comments")
    public ResponseEntity<List<Comment>> getUserComments() {
        User user = userService.getUserWithAuthority();
        List<Comment> comments = commentRepository.findByUserId(user.getId());
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

}
