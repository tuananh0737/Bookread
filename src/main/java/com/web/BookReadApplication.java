package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BookReadApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookReadApplication.class, args);
    }

}
