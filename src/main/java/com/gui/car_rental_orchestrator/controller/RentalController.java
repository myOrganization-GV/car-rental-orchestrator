package com.gui.car_rental_orchestrator.controller;

import com.gui.car_rental_common.dtos.BookingDto;
import com.gui.car_rental_orchestrator.services.RentalSagaOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RentalController {

    private final RentalSagaOrchestratorService orchestratorService;

    public RentalController(RentalSagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }
    @PostMapping("/rent/car")
    public ResponseEntity<String> rentCar(@RequestBody BookingDto bookingDto) {
        orchestratorService.startCarReservationSaga(bookingDto);
        return new ResponseEntity<>("NOW RUNNING FROM GOOGLE VM:Car reservation saga initiated for car ID: " + bookingDto.getCarId(), HttpStatus.ACCEPTED);
    }
}
