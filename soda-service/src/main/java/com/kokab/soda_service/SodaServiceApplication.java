package com.kokab.soda_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SodaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SodaServiceApplication.class, args);
	}

}