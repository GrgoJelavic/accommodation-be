package com.evoapartments.accommodationbe.controller.accommodation;

import com.evoapartments.accommodationbe.exception.PhotoRetrievalException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.response.ReservedAccommodationResponse;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.service.reservation.IReservedAccommodationService;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodations")
public class AccommodationController {
    private final IAccommodationService accommodationService;
    private final IReservedAccommodationService accommodationReservedService;

    @GetMapping("/all-accommodations")
    public ResponseEntity<HttpResponse> getAllAccommodations() throws SQLException {
        List<Accommodation> accommodations = accommodationService.getAllAccommodations();
        List<AccommodationResponse> accommodationResponses = setPhotosIntoAccommodationResponses(accommodations);
        return accommodationResponses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("accommodations", accommodationResponses, "size", accommodations.size()))
                                .message("Accommodations list fetched successfully.")
                                .status(HttpStatus.FOUND)
                                .statusCode(HttpStatus.FOUND.value())
                                .build());
    }

    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<?> getAccommodationById(@PathVariable Long accommodationId) {
        Optional<Accommodation> accommodation = accommodationService.getAccommodationById(accommodationId);
        return accommodation.map(a -> {
            AccommodationResponse accommodationResponse;
            try {
                accommodationResponse = setMainPhotoIntoAccommodationResponse(a);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("accommodation", accommodationResponse))
                            .message("Accommodation found successfully.")
                            .status(HttpStatus.FOUND)
                            .statusCode(HttpStatus.FOUND.value())
                            .build());
        }).orElseThrow(() -> new ResourceNotFoundException("Accommodation not found."));
    }

    @GetMapping("/names")
    public ResponseEntity<HttpResponse> getAccommodationNames(){
        List<String> accommodationNames = accommodationService.getAllAccommodationNames();
        return accommodationNames.isEmpty()
                ? ResponseEntity.noContent().build()
                :  ResponseEntity.created(URI.create("")).body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("accommodationNames", accommodationNames, "size", accommodationNames.size()))
                                .message("Accommodation names list fetched successfully.")
                                .status(HttpStatus.FOUND)
                                .statusCode(HttpStatus.FOUND.value())
                                .build());
    }

    @GetMapping("/available-accommodations")
    public ResponseEntity<?> getAvailableAccommodations(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam("accommodationType") Long typeId) throws SQLException {
        List<Accommodation> availableAccommodations = accommodationService.getAvailableAccommodations(checkIn, checkOut, typeId);
        List<AccommodationResponse> accommodationResponses = setPhotosIntoAccommodationResponses(availableAccommodations);
        return accommodationResponses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("accommodations", accommodationResponses, "size", availableAccommodations.size()))
                        .message("Available accommodations list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PostMapping("/add/new-accommodation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> addNewAccommodation(
            @RequestParam("accommodationTypeId")Long accommodationTypeId,
            @RequestParam("accommodationPrice")BigDecimal accommodationPrice,
            @RequestParam("photo")MultipartFile photo,
            @RequestParam("accommodationName")String accommodationName,
            @RequestParam("guestCapacity")Integer guestCapacity,
            @RequestParam("address")String address,
            @RequestParam("city")String city,
            @RequestParam("zipCode")Integer zipCode,
            @RequestParam("country")String country
            ) throws SQLException, IOException {
        Accommodation savedAccommodation = accommodationService.addNewAccommodation(accommodationTypeId,
                accommodationPrice, photo, accommodationName, guestCapacity, address, city, zipCode, country );
        AccommodationResponse response = new AccommodationResponse(
                savedAccommodation.getId(),
                savedAccommodation.getAccommodationPrice(),
                savedAccommodation.getAccommodationName(),
                savedAccommodation.getGuestCapacity(),
                savedAccommodation.getAddress(),
                savedAccommodation.getCity(),
                savedAccommodation.getZipCode(),
                savedAccommodation.getCountry(),
                savedAccommodation.getType());
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("newAccommodation", response))
                        .message("New accommodation was created successfully.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }

    @PutMapping("/update/accommodation/{accommodationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> updateAccommodation(@PathVariable Long accommodationId,
                                                                     @RequestParam(required = false) Long accommodationTypeId,
                                                                     @RequestParam(required = false) BigDecimal accommodationPrice,
                                                                     @RequestParam(required = false) MultipartFile photo,
                                                                     @RequestParam(required = false) String accommodationName,
                                                                     @RequestParam(required = false) Integer guestCapacity,
                                                                     @RequestParam(required = false) String address,
                                                                     @RequestParam(required = false) String city,
                                                                     @RequestParam(required = false) Integer zipCode,
                                                                     @RequestParam(required = false) String country) throws SQLException, IOException {
        byte[] photoBytes = photo != null && !photo.isEmpty()
                ? photo.getBytes()
                : accommodationService.getAccommodationMainPhotoByAccommodationId(accommodationId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0
                ? new SerialBlob(photoBytes)
                : null;
        Accommodation theAccommodation = accommodationService.updateAccommodation(accommodationId, accommodationTypeId,
                accommodationPrice, photoBytes, accommodationName, guestCapacity, address, city, zipCode, country);
        theAccommodation.setPhoto(photoBlob);
        AccommodationResponse accommodationResponse = getAccommodationResponse(theAccommodation);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("updatedAccommodation", accommodationResponse))
                        .message("New accommodation was updated successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/delete/accommodation/{accommodationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> deleteAccommodation(@PathVariable Long accommodationId){
        accommodationService.deleteAccommodation(accommodationId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("The accommodation was deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build());
    }

    private AccommodationResponse getAccommodationResponse(Accommodation accommodation) {
        List<ReservedAccommodation> reservations = getAllAccommodationReservationsByAccommodationId(accommodation.getId());
        List<ReservedAccommodationResponse> reservationsInfo = reservations
                .stream()
                .map(reservation -> new ReservedAccommodationResponse(
                        reservation.getId(),
                        reservation.getCheckInDate(),
                        reservation.getCheckOutDate(),
                        reservation.getReservationConfirmationCode()))
                .toList();
        byte[] photoBytes = null;
        Blob photoBlob = accommodation.getPhoto();
        if (photoBlob != null){
            try {
                photoBytes = photoBlob.getBytes(1,(int)photoBlob.length());
            }catch (SQLException ex){
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new AccommodationResponse(accommodation.getId(), accommodation.getAccommodationPrice(),
                accommodation.isReserved(), photoBytes, accommodation.getAccommodationName(),
                accommodation.getGuestCapacity(), accommodation.getAddress(), accommodation.getCity(),
                accommodation.getZipCode(), accommodation.getCountry(), accommodation.getType(), reservationsInfo);
    }

    private List<AccommodationResponse> setPhotosIntoAccommodationResponses(List<Accommodation> accommodations) throws SQLException {
        List<AccommodationResponse> accommodationResponses = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            AccommodationResponse accommodationResponse = setMainPhotoIntoAccommodationResponse(accommodation);
            accommodationResponses.add(accommodationResponse);
        }
        return accommodationResponses;
    }

    private AccommodationResponse setMainPhotoIntoAccommodationResponse(Accommodation accommodation) throws SQLException {
        AccommodationResponse accommodationResponse = getAccommodationResponse(accommodation);
        byte[] photoBytes = accommodationService.getAccommodationMainPhotoByAccommodationId(accommodation.getId());
        if (photoBytes != null && photoBytes.length > 0) {
            String base64Photo = Base64.encodeBase64String(photoBytes);
            accommodationResponse.setPhoto(base64Photo);
        }
        return accommodationResponse;
    }

    private List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId) {
        return accommodationReservedService.getAllAccommodationReservationsByAccommodationId(accommodationId);
    }
}
