package com.web.repository;

import com.web.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {
    List<BorrowRequest> findByBookIdAndNotifiedFalse(Long bookId);
}
