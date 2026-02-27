package com.web.repository;

import com.web.entity.Book;
import com.web.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenresRepository extends JpaRepository<Genres,Long> {
    @Query("select g from Genres g where LOWER(g.name) like LOWER(?1)")
    public List<Genres> findByParam(String param);

}

