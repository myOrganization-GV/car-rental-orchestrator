package com.gui.car_rental_orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CarRentalOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalOrchestratorApplication.class, args);
	}

}
