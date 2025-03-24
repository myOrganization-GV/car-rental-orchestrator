package com.gui.car_rental_orchestrator.services;

import com.gui.car_rental_common.commands.ReserveCarCommand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
public class RentalSagaOrchestratorService {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RentalSagaOrchestratorService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startCarReservationSaga(UUID carId) {
        // 1. Generate a unique Saga Transaction ID
        String sagaTransactionId = UUID.randomUUID().toString();
        System.out.println("Starting Car Reservation Saga with ID: " + sagaTransactionId);

        // 2. Create the ReserveCarCommand
        ReserveCarCommand reserveCarCommand = new ReserveCarCommand(carId, sagaTransactionId);

        // 3. Send the ReserveCarCommand to the Inventory Service
        kafkaTemplate.send("rental-saga-commands", reserveCarCommand); // Use the command topic

        System.out.println("Sent ReserveCarCommand for Car ID: " + carId + ", Saga ID: " + sagaTransactionId);

        // In the next steps, we'll listen for the CarReservedEvent or CarReservationFailedEvent
    }

}
