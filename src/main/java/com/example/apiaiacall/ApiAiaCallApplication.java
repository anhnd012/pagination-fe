package com.example.apiaiacall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;

import reactor.core.publisher.Mono;


@SpringBootApplication
public class ApiAiaCallApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAiaCallApplication.class, args);


	}

}
