package com.example.secureapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecureAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureAppApplication.class, args);
    }
}
