package com.web.repository;

import com.web.entity.Author;
import com.web.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Query("select a from Author a where a.fullname like ?1")
    public List<Author> findByParam(String param);
}
