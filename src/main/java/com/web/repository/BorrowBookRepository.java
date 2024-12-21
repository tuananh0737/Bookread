package com.web.repository;

import com.web.entity.BorrowBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, Long> {

    @Query("select b from BorrowBook b where b.user.id = ?1 and b.book.id = ?2")
    public Optional<BorrowBook> findByUserAndBook(Long userId, Long bookId);

    @Query("select b from BorrowBook b where b.user.id = ?1")
    public List<BorrowBook> findByUser(Long userId);

    @Query("SELECT COUNT(b) FROM BorrowBook b")
    long countTotalBorrowed();

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.returned = true AND b.actualReturnDate <= b.returnDueDate")
    long countReturnedOnTime();

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.returned = true AND b.actualReturnDate > b.returnDueDate")
    long countReturnedLate();

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.returned = false")
    long countNotReturned();

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.user.id = ?1")
    long countByUser(Long userId);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.user.id = ?1 AND b.returned = true AND b.actualReturnDate <= b.returnDueDate")
    long countByUserAndReturnedOnTime(Long userId);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.user.id = ?1 AND b.returned = true AND b.actualReturnDate > b.returnDueDate")
    long countByUserAndReturnedLate(Long userId);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.user.id = ?1 AND b.returned = false")
    long countByUserAndNotReturned(Long userId);

    @Query("SELECT b FROM BorrowBook b WHERE b.returned = false AND b.returnDueDate BETWEEN ?1 AND ?2")
    List<BorrowBook> findBooksDueBetween(Timestamp start, Timestamp end);

    @Query("SELECT b FROM BorrowBook b WHERE b.returned = false AND b.returnDueDate < ?1")
    List<BorrowBook> findBooksOverdue(Timestamp now);

}
