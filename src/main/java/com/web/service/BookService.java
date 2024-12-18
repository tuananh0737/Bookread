package com.web.service;

import com.web.entity.Book;
import com.web.repository.BookRepository;
import com.web.config.MessageException;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new MessageException("Sách không tồn tại", 404));
    }
}
