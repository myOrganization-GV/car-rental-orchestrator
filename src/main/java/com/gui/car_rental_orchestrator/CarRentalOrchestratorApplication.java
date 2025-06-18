package com.gui.car_rental_orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodSecurity
public class CarRentalOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalOrchestratorApplication.class, args);
	}

}
