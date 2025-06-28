package com.saisai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SaisaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaisaiApplication.class, args);
    }

}
