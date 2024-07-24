package com.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearch {

    private Long genreId;

    private Long authorId;

    private String param;
}
