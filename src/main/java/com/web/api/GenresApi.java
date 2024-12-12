package com.web.api;

import com.web.dto.AuthorSearch;
import com.web.dto.GenreSearch;
import com.web.dto.SearchByGenres;
import com.web.entity.Author;
import com.web.entity.Book;
import com.web.entity.Genres;
import com.web.repository.BookRepository;
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

    @Autowired
    private BookRepository bookRepository;

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

    @PostMapping("/public/search-by-genres")
    public List<Book> search(@RequestBody SearchByGenres searchByGenres) {
        String param = searchByGenres.getParam();
        if (param == null) {
            param = "%";
        }
        param = "%" + param + "%";
        List<Book> list = null;
        if(param != null) {
            list = bookRepository.findByGenres(param);
        }
        return list;
    }

    @PostMapping("/public/search-genre")
    public List<Genres> search(@RequestBody GenreSearch genreSearch) {
        String param = genreSearch.getParam();
        if(param == null){
            param = "";
        }
        param ="%" + param +"%" ;
        List<Genres> list = null;
        if(param != null){
            list = genresRepository.findByParam(param);
        }
        return list;
    }
}
