package com.example.ddingsroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DdingsroomApplication {
    public static void main(String[] args) {
        SpringApplication.run(DdingsroomApplication.class, args);
    }

}

