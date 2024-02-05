package com.evoapartments.accommodationbe.resource.reservation;

import com.evoapartments.accommodationbe.exception.InvalidReservationRequestException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.response.ReservedAccommodationResponse;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.service.reservation.IReservedAccommodationService;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservedAccommodationController {
    private final IReservedAccommodationService accommodationReservedService;
    private final IAccommodationService accommodationService;

    @GetMapping("/all-reservations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReservedAccommodationResponse>> getAllReservations() {
        List<ReservedAccommodation> reservations = accommodationReservedService.getAllReservations();
        List<ReservedAccommodationResponse> accommodationReservedResponses = new ArrayList<>();
        for (ReservedAccommodation accommodation : reservations) {
            ReservedAccommodationResponse reservedAccommodationResponse = getReservationResponse(accommodation);
            accommodationReservedResponses.add(reservedAccommodationResponse);
        }
        return ResponseEntity.ok(accommodationReservedResponses);
    }

    @GetMapping("confirmation/{confirmationCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<?> getReservationByConfirmationCode(@PathVariable String confirmationCode){
        try {
            ReservedAccommodation reservation = accommodationReservedService.findByReservationConfirmationCode(confirmationCode);
            ReservedAccommodationResponse reservationResponse = getReservationResponse(reservation);
            return ResponseEntity.ok(reservationResponse);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<ReservedAccommodationResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<ReservedAccommodation> bookings = accommodationReservedService.getReservationsByUsername(email);
        List<ReservedAccommodationResponse> bookingResponses = new ArrayList<>();
        for (ReservedAccommodation booking : bookings) {
            ReservedAccommodationResponse bookingResponse = getReservationResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/accommodation/{accommodationId}/reservation")
    public ResponseEntity<?> saveReservation(@PathVariable Long accommodationId,
                                             @RequestBody ReservedAccommodation reservationRequest){
        System.out.println(reservationRequest);
        try {
            String confirmationCode = accommodationReservedService.saveReservation(accommodationId, reservationRequest);
            return ResponseEntity.ok("Accommodation reserved successfully. Reservation confirmation code is : " +confirmationCode);
        } catch (InvalidReservationRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    @DeleteMapping("/reservation/{reservationId}/delete")
    public void cancelReservation(@PathVariable("reservationId") Long reservationId){
        accommodationReservedService.cancelReservation(reservationId);
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

