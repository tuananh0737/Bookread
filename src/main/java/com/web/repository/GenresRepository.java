package com.web.repository;

import com.web.entity.Author;
import com.web.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenresRepository extends JpaRepository<Genres,Long> {
}
