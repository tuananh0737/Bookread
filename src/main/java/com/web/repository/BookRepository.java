package com.web.repository;

import com.web.entity.Book;
import com.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("select b from Book b where b.name like ?1 or b.author.fullname like ?1 or b.genres.name like ?1")
    public List<Book> findByParam(String param);

    @Query("select b from Book b where (b.name like ?1 or b.author.fullname like ?1 or b.genres.name like ?1) and b.genres.id = ?2")
    public List<Book> findByParamAndGenre(String param, Long genreId);

    @Query("select b from Book b where (b.name like ?1 or b.author.fullname like ?1 or b.genres.name like ?1) and b.author.id = ?2")
    public List<Book> findByParamAndAuthor(String param, Long authorId);

    @Query("select b from Book b where (b.name like ?1 or b.author.fullname like ?1 or b.genres.name like ?1) and b.author.id = ?2 and b.genres.id = ?3")
    public List<Book> findByParamAndAuthorAndGenre(String param, Long authorId, Long genreId);

    @Query("select b from Book b where b.genres.name like ?1")
    public List<Book> findByGenres(String param);

    @Query("select b from Book b where b.location.id = ?1")
    public List<Book> findByBookLocation(Long locationId);

    @Query("select b from Book b order by b.averageRating desc")
    public List<Book> findAllOrderByAverageRatingDesc();

    @Query("SELECT SUM(b.quantity) FROM Book b")
    long countTotalAvailableBooks();

}
