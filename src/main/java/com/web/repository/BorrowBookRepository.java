package com.web.repository;

import com.web.entity.BorrowBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, Long> {

    @Query("select b from BorrowBook b where b.user.id = ?1 and b.book.id = ?2")
    public Optional<BorrowBook> findByUserAndBook(Long userId, Long bookId);

    @Query("select b from BorrowBook b where b.user.id = ?1")
    public List<BorrowBook> findByUser(Long userId);
}
