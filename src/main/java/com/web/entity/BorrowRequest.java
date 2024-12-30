package com.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "borrow_request")
@Getter
@Setter
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "notified", nullable = false)
    private Boolean notified = false; // Đánh dấu đã gửi thông báo hay chưa
}
