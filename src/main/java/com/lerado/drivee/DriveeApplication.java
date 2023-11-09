package com.lerado.drivee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.lerado.drivee.services.StorageService;

@SpringBootApplication
public class DriveeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriveeApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			// storageService.deleteAll();
			storageService.init();
		};
	}
}
