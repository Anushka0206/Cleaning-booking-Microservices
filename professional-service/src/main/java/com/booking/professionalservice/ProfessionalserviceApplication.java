package com.booking.professionalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ProfessionalserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfessionalserviceApplication.class, args);
	}

}
