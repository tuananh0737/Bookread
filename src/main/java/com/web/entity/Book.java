package com.web.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    private Integer numberPage;

    private Integer publishYear;

    private String description;

    private Float averageRating;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "genres_id")
    private Genres genres;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @Lob
    @Column(name = "qr_code")
    private byte[] qrCode;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private BookLocation location;

}
