package com.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Timestamp createdDate;
    private Float star;
    private String bookName;
}
