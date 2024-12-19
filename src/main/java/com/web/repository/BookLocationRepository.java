package com.web.repository;

import com.web.entity.BookLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLocationRepository extends JpaRepository<BookLocation, Long> {
}
