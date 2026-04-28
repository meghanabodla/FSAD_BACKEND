package com.klu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssignmentBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentBackendApplication.class, args);
    }
}
