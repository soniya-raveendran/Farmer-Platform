package com.farmer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableAsync
public class FarmerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmerApplication.class, args);
	}

}
