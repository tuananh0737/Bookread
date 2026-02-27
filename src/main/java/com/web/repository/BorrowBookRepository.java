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

    // Đã sửa cú pháp MONTH(), YEAR() thành EXTRACT() chuẩn PostgreSQL
    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE EXTRACT(MONTH FROM b.createdDate) = ?1 AND EXTRACT(YEAR FROM b.createdDate) = ?2")
    long countTotalBorrowedByMonth(int month, int year);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE EXTRACT(MONTH FROM b.actualReturnDate) = ?1 AND EXTRACT(YEAR FROM b.actualReturnDate) = ?2 AND b.returned = true AND b.actualReturnDate <= b.returnDueDate")
    long countReturnedOnTimeByMonth(int month, int year);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE EXTRACT(MONTH FROM b.actualReturnDate) = ?1 AND EXTRACT(YEAR FROM b.actualReturnDate) = ?2 AND b.returned = true AND b.actualReturnDate > b.returnDueDate")
    long countReturnedLateByMonth(int month, int year);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.returned = false AND EXTRACT(MONTH FROM b.returnDueDate) = ?1 AND EXTRACT(YEAR FROM b.returnDueDate) = ?2")
    long countNotReturnedByMonth(int month, int year);

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.createdDate BETWEEN ?1 AND ?2")
    long countTotalBorrowedBetween(Timestamp start, Timestamp end);

    // Đã sửa tên bảng thành borrow_book và dùng EXTRACT(EPOCH) để tính khoảng cách ngày
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (b.actual_return_date - b.created_date)) / 86400) FROM borrow_book b WHERE b.returned = true", nativeQuery = true)
    Double calculateAverageBorrowTime();

    @Query("SELECT COUNT(b) FROM BorrowBook b WHERE b.returned = false")
    long countBooksCurrentlyBorrowed();

    // Đã thêm b.book.name vào mệnh đề GROUP BY
    @Query("SELECT b.book.name, COUNT(b) as borrowCount FROM BorrowBook b GROUP BY b.book.id, b.book.name ORDER BY borrowCount DESC")
    List<Object[]> findTopBooks();

    // Đã thêm b.user.fullname vào mệnh đề GROUP BY
    @Query("SELECT b.user.fullname, COUNT(b) as borrowCount FROM BorrowBook b GROUP BY b.user.id, b.user.fullname ORDER BY borrowCount DESC")
    List<Object[]> findTopUsers();

}