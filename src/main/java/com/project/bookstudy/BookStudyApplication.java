package com.project.bookstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BookStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStudyApplication.class, args);
	}

}
