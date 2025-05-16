package com.gui.car_rental_orchestrator.services;

import com.gui.car_rental_common.commands.*;
import com.gui.car_rental_common.dtos.BookingDto;
import com.gui.car_rental_common.events.booking.BookingCancellationFailedEvent;
import com.gui.car_rental_common.events.booking.BookingCancelledEvent;
import com.gui.car_rental_common.events.booking.BookingCreatedEvent;
import com.gui.car_rental_common.events.booking.BookingCreationFailedEvent;
import com.gui.car_rental_common.events.inventory.CancelCarReservationFailedEvent;
import com.gui.car_rental_common.events.inventory.CarReservationCancelledEvent;
import com.gui.car_rental_common.events.inventory.CarReservationFailedEvent;
import com.gui.car_rental_common.events.inventory.CarReservedEvent;
import com.gui.car_rental_common.events.payment.PaymentCreatedEvent;
import com.gui.car_rental_common.events.payment.PaymentCreationFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
@KafkaListener(topics = {"payment-service-events","booking-service-events","inventory-service-events"},groupId = "orchestrator-group")
public class RentalSagaOrchestratorService {
    private static final Logger logger = LoggerFactory.getLogger(RentalSagaOrchestratorService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RentalSagaOrchestratorService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startCarReservationSaga(BookingDto bookingDto) {
        UUID sagaTransactionId = UUID.randomUUID();
        logger.info("Starting Car Reservation Saga with ID: {}", sagaTransactionId);
        ReserveCarCommand reserveCarCommand = new ReserveCarCommand(bookingDto ,sagaTransactionId);
        kafkaTemplate.send("rental-saga-inventory-commands", reserveCarCommand);
        logger.info("Sent ReserveCarCommand with Saga ID: {}", sagaTransactionId);
    }


    //events

    @KafkaHandler
    public void handleCarReservationFailedEvent(CarReservationFailedEvent event){
        logger.info("CAR RESERVATION FAILED");
    }
    @KafkaHandler
    public void handleCarReservedEvent(CarReservedEvent event) {
        logger.info("Received CarReservedEvent for Saga ID: {}", event.getSagaTransactionId());
        logger.info("Starting next saga step, create new booking for Saga ID: {}", event.getSagaTransactionId());
        BookingCreationCommand bookingCreationCommand = new BookingCreationCommand(event.getSagaTransactionId(), event.getBookingDto());
        kafkaTemplate.send("rental-saga-booking-commands", bookingCreationCommand);
        logger.info("Sent BookingCreationCommand with Saga ID: {}", event.getSagaTransactionId());
    }

    @KafkaHandler
    public void handleCarReservationCancelledEvent(CarReservationCancelledEvent event) {
        logger.info("CAR RESERVATION CANCELLED FOR SAGA ID: {}", event.getSagaTransactionId());
    }

    @KafkaHandler
    public void handleCarReservationCancelledFailedEvent(CancelCarReservationFailedEvent event) {
        logger.info("Cancel car reservation failed for saga with id: {}", event.getSagaTransactionId());
        logger.info("Car with id: {} expected to be available but it not check the inventory service.", event.getSagaTransactionId());
    }


    @KafkaHandler
    public void handleBookingCreatedEvent(BookingCreatedEvent event){
        logger.info("Processing BookingCreatedEvent for Saga ID: {}", event.getSagaTransactionId());
        logger.info("BookingCreatedEvent for Saga ID: {} received successfully, starting next step creating payment...", event.getSagaTransactionId());
        PaymentCreationCommand paymentCreationCommand = new PaymentCreationCommand(event.getSagaTransactionId(), event.getBookingDto());
        kafkaTemplate.send("rental-saga-payment-commands", paymentCreationCommand);
        logger.info("Sent PaymentCreationCommand with Saga ID: {} ", event.getSagaTransactionId());
    }



    @KafkaHandler
    public void handleBookingCreationFailedEvent(BookingCreationFailedEvent bookingCreationFailedEvent){
        logger.info("Received BookingCreationFailedEvent for Saga ID: {}", bookingCreationFailedEvent.getSagaTransactionId());
        CancelCarReservationCommand cancelCarReservationCommand = new CancelCarReservationCommand(bookingCreationFailedEvent.getSagaTransactionId(), bookingCreationFailedEvent.getBookingDto());
        kafkaTemplate.send("rental-saga-inventory-commands", cancelCarReservationCommand);
        logger.info("Sent cancelCarReservationCommand with Saga ID: {}", cancelCarReservationCommand.getSagaTransactionId());

    }
    @KafkaHandler
    public void handleBookingCancelledEvent (BookingCancelledEvent event) {
        logger.info("BOOKING CANCELLED FOR SAGA ID: {}", event.getSagaTransactionId());
    }

    @KafkaHandler
    public void handleBookingCancellationFailedEvent (BookingCancellationFailedEvent event) {
        logger.info("Booking cancellation failed for saga Id: {}", event.getSagaTransactionId());
        logger.info("Booking with id: {} expected to be cancelled but is not.", event.getBookingDto().getBookingId());
    }


    @KafkaHandler
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event){
        logger.info("Processing PaymentCreatedEvent for Saga ID: {}", event.getSagaTransactionId());
        logger.info("PaymentCreatedEvent for Saga ID: {} received successfully...", event.getSagaTransactionId());
        logger.info("Car Rental Saga transaction completed. Saga ID: {} ", event.getSagaTransactionId());
    }
    @KafkaHandler
    public void handlePaymentCreationFailedEvent(PaymentCreationFailedEvent event){
       logger.info("Received PaymentCreationFailedEvent for Saga ID: {}", event.getSagaTransactionId());
       CancelCarReservationCommand cancelCarReservationCommand = new CancelCarReservationCommand(event.getSagaTransactionId(), event.getBookingDto());
       kafkaTemplate.send("rental-saga-inventory-commands", cancelCarReservationCommand);
       logger.info("Sent cancelCarReservationCommand with Saga ID: {}", cancelCarReservationCommand.getSagaTransactionId());
       CancelBookingCommand cancelBookingCommand = new CancelBookingCommand(event.getSagaTransactionId(), event.getBookingDto());
       kafkaTemplate.send("rental-saga-booking-commands", cancelBookingCommand);
       logger.info("Sent CancelBookingCommand with Saga ID: {}", cancelBookingCommand.getSagaTransactionId());
       logger.info("Saga transaction cancelled at payment step with SagaID: {}", event.getSagaTransactionId());
    }





}
