package com.example.studentplacementmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudentPlacementManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentPlacementManagementApplication.class, args);
	}

}
