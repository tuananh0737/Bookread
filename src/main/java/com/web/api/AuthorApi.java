package com.web.api;

import com.web.entity.Author;
import com.web.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AuthorApi {

    @Autowired
    private AuthorRepository authorRepository;

    @PostMapping("/admin/add-update-author")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Author author) {
        Author result = authorRepository.save(author);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/public/find-all-author")
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @DeleteMapping("/admin/delete-author")
    public void delete(@RequestParam("id") Long id) {
        authorRepository.deleteById(id);
    }
}
