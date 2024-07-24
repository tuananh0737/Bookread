package com.web.api;

import com.web.entity.Author;
import com.web.entity.Genres;
import com.web.repository.GenresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GenresApi {

    @Autowired
    private GenresRepository genresRepository;

    @PostMapping("/admin/add-update-genres")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Genres genres) {
        Genres result = genresRepository.save(genres);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/public/find-all-genres")
    public List<Genres> findAll() {
        return genresRepository.findAll();
    }

    @DeleteMapping("/admin/delete-genres")
    public void delete(@RequestParam("id") Long id) {
        genresRepository.deleteById(id);
    }
}
