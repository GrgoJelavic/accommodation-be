package com.evoapartments.accommodationbe.controller.reservation;

import com.evoapartments.accommodationbe.exception.InvalidReservationRequestException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.response.ReservedAccommodationResponse;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.service.reservation.IReservedAccommodationService;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservedAccommodationController {
    private final IReservedAccommodationService accommodationReservedService;
    private final IAccommodationService accommodationService;

    @GetMapping("/all-reservations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> getAllReservations() {
        List<ReservedAccommodation> reservations = accommodationReservedService.getAllReservations();
        List<ReservedAccommodationResponse> accommodationReservedResponses = new ArrayList<>();
        for (ReservedAccommodation accommodation : reservations) {
            ReservedAccommodationResponse reservedAccommodationResponse = getReservationResponse(accommodation);
            accommodationReservedResponses.add(reservedAccommodationResponse);
        }
        return accommodationReservedResponses.isEmpty()
                ? ResponseEntity.noContent().build()
                :  ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("reservations", accommodationReservedResponses))
                        .message("Reservations list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());    }

    @GetMapping("confirmation/{confirmationCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<?> getReservationByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            ReservedAccommodation reservation = accommodationReservedService.findByReservationConfirmationCode(confirmationCode);
            ReservedAccommodationResponse reservationResponse = getReservationResponse(reservation);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("reservation", reservationResponse))
                            .message("Reservation fetched successfully.")
                            .status(HttpStatus.FOUND)
                            .statusCode(HttpStatus.FOUND.value())
                            .build());
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<HttpResponse> getBookingsByUserEmail(@PathVariable String email) {
        List<ReservedAccommodation> bookings = accommodationReservedService.getReservationsByUsername(email);
        List<ReservedAccommodationResponse> bookingResponses = new ArrayList<>();
        for (ReservedAccommodation booking : bookings) {
            ReservedAccommodationResponse bookingResponse = getReservationResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return bookingResponses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("reservation", bookingResponses))
                        .message("User reservations fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PostMapping("/accommodation/{accommodationId}/reservation")
    public ResponseEntity<?> saveReservation(@PathVariable Long accommodationId,
                                             @RequestBody ReservedAccommodation reservationRequest) {
        try {
            String confirmationCode = accommodationReservedService.saveReservation(accommodationId, reservationRequest);
            ReservedAccommodationResponse response = getReservationResponse(reservationRequest);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("reservation", response))
                            .message("Accommodation reserved successfully. Confirmation code is: " + confirmationCode)
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        } catch (InvalidReservationRequestException | GeneralSecurityException | IOException ex ) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    @DeleteMapping("/reservation/{reservationId}/delete")
    public ResponseEntity<HttpResponse> cancelReservation(@PathVariable("reservationId") Long reservationId) throws IOException {
        accommodationReservedService.cancelReservation(reservationId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Reservation with ID no:" + reservationId + " is canceled successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
     }

    private ReservedAccommodationResponse getReservationResponse(ReservedAccommodation reservation) {
        Accommodation theAccommodation = accommodationService.getAccommodationById(reservation.getAccommodation().getId()).get();
        AccommodationResponse accommodationResponse = new AccommodationResponse(
                theAccommodation.getId(),
                theAccommodation.getAccommodationPrice(),
                theAccommodation.getAccommodationName(),
                theAccommodation.getGuestCapacity(),
                theAccommodation.getAddress(),
                theAccommodation.getCity(),
                theAccommodation.getZipCode(),
                theAccommodation.getCountry(),
                theAccommodation.getType());
        return new ReservedAccommodationResponse(
                reservation.getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getGuestFullName(),
                reservation.getGuestEmail(),
                reservation.getNumberOfAdults(),
                reservation.getNumberOfChildren(),
                reservation.getTotalNumberOfGuests(),
                reservation.getReservationConfirmationCode(),
                accommodationResponse
        );
    }
}

