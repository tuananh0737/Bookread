package com.web.api;

import com.web.config.MessageException;
import com.web.entity.Author;
import com.web.entity.BookMark;
import com.web.entity.Comment;
import com.web.entity.User;
import com.web.repository.BookMarkRepository;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class BookMarkApi {

    @Autowired
    private BookMarkRepository bookMarkRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/user/add-bookmark")
    public ResponseEntity<?> save(@RequestBody BookMark bookMark) {
        User user = userService.getUserWithAuthority();
        if(bookMarkRepository.findByUserAndBook(user.getId(), bookMark.getBook().getId()).isPresent()){
            throw new MessageException("Sách này đã được thêm vào yêu thích");
        }
        bookMark.setUser(user);
        BookMark result = bookMarkRepository.save(bookMark);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/delete-bookmark")
    public void delete(@RequestParam("id") Long id) {
        User user = userService.getUserWithAuthority();
        BookMark bookMark = bookMarkRepository.findById(id).get();
        if(bookMark.getUser().getId() != user.getId()){
            throw new MessageException("Bạn không đủ quyền");
        }
        bookMarkRepository.deleteById(id);
    }

    @GetMapping("/user/find-bookmark-by-user")
    public List<BookMark> findByUser() {
        User user = userService.getUserWithAuthority();
        return bookMarkRepository.findByUser(user.getId());
    }
}
