package com.stamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionStamosApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionStamosApplication.class, args);
    }
}