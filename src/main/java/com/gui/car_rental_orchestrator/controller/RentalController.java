package com.gui.car_rental_orchestrator.controller;

import com.gui.car_rental_common.dtos.BookingDto;
import com.gui.car_rental_orchestrator.services.RentalSagaOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class RentalController {

    private final RentalSagaOrchestratorService orchestratorService;

    public RentalController(RentalSagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }
    @GetMapping("/rent/all")
    public String helloAll() {
        return "All public access";
    }
    @GetMapping("/rent/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloAdmineController() {
        return "Admin level access";
    }
    @GetMapping("/rent/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String helloUserController(){
        return "User access level";
    }
    @PostMapping("/rent/car")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> rentCar(@RequestBody BookingDto bookingDto, Authentication auth) {
        String userEmail = extractEmailFromAuth(auth);
        if (userEmail == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email not available in authentication token"
            );
        }

        bookingDto.setEmail(userEmail);
        orchestratorService.startCarReservationSaga(bookingDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Car reservation saga initiated");
        response.put("carId", bookingDto.getCarId());
        response.put("userEmail", bookingDto.getEmail());

        return ResponseEntity.accepted().body(response);
    }
    private String extractEmailFromAuth(Authentication auth){
        String email = null;
        if(auth instanceof JwtAuthenticationToken){
            return email = ((JwtAuthenticationToken) auth).getToken().getClaim("email");
        }
        if(auth instanceof BearerTokenAuthentication){
            return email = ((BearerTokenAuthentication) auth).getTokenAttributes().get("email").toString();
        }

        return email;
    }


}
