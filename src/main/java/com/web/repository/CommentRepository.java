package com.web.repository;

import com.web.entity.Author;
import com.web.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c where c.book.id = ?1")
    public List<Comment> findByBook(Long bookId);

    @Query("SELECT AVG(c.star) FROM Comment c WHERE c.book.id = ?1")
    public Float findAverageStarByBookId(Long bookId);
}
