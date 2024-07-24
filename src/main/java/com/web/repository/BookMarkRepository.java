package com.web.repository;

import com.web.entity.Author;
import com.web.entity.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark,Long> {

    @Query("select b from BookMark b where b.user.id = ?1 and b.book.id = ?2")
    public Optional<BookMark> findByUserAndBook(Long userId, Long bookId);

    @Query("select b from BookMark b where b.user.id = ?1")
    public List<BookMark> findByUser(Long userId);
}
