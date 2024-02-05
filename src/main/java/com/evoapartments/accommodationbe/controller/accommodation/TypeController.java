package com.evoapartments.accommodationbe.controller.accommodation;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.accommodation.Type;
import com.evoapartments.accommodationbe.exception.TypeAlreadyExistsException;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.response.TypeResponse;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import com.evoapartments.accommodationbe.service.accommodation.ITypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodation-types")
public class TypeController {
    private final ITypeService accommodationTypeService;
    private final IAccommodationService accommodationService;

    @GetMapping("/all-types")
    public ResponseEntity<HttpResponse> getAllAccommodationTypes(){
        List<Type> typeList = accommodationTypeService.getAllAccommodationTypes();
        return typeList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("accommodationTypes", typeList, "size", typeList.size()))
                        .message("Accommodations list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @GetMapping("/all-types-with-accommodations")
        public ResponseEntity<HttpResponse> getAllAccommodationTypesWithAccommodations(){
        List<Type> types = accommodationTypeService.getAllAccommodationTypesWithAccommodations();
        List<TypeResponse> typesResponses = new ArrayList<>();
        for (Type type : types) {
            TypeResponse accommodationTypeResponse = getAccommodationTypesWithAccommodationsResponse(type);
            typesResponses.add(accommodationTypeResponse);
        }
        return typesResponses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("accommodationTypes", typesResponses, "size", typesResponses.size()))
                            .message("Accommodation types list fetched successfully.")
                            .status(HttpStatus.FOUND)
                            .statusCode(HttpStatus.FOUND.value())
                            .build());
    }

    @GetMapping("/type-names")
    public ResponseEntity<HttpResponse> getAccommodationTypeNames(){
        List<String> typeNames = accommodationTypeService.getAllAccommodationTypeNames();
        return typeNames.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("accommodations", typeNames, "size", typeNames.size()))
                                .message("Accommodations list fetched successfully.")
                                .status(HttpStatus.FOUND)
                                .statusCode(HttpStatus.FOUND.value())
                                .build());
    }

    @PostMapping("/create-new-accommodation-type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createAccommodationType(@RequestBody Type type){
        try {
            accommodationTypeService.createAccommodationType(type);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("newAccommodationType", type))
                            .message("New accommodation type created successfully.")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build());
        } catch (TypeAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/update/accommodation-type/{typeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> updateAccommodationType(@PathVariable Long typeId,
        @RequestParam(required = false) String typeName, @RequestParam(required = false) String description) {
        Type theType = accommodationTypeService.updateAccommodationType(typeId, typeName, description);
        TypeResponse typeResponse = getAccommodationTypeResponse(theType);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("newAccommodationType", typeResponse))
                        .message("New accommodation type updated successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/delete/accommodation-type/{typeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> deleteAccommodationType(@PathVariable Long typeId){
        accommodationTypeService.deleteAccommodationType(typeId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("The accommodation type was deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build());
    }

    private TypeResponse getAccommodationTypeResponse(Type type) {
        return new TypeResponse(type.getId(), type.getTypeName(), type.getDescription());
    }

    private TypeResponse getAccommodationTypesWithAccommodationsResponse(Type type) {
        List<Accommodation> accommodations = accommodationService.getAccommodationsByTypeId(type.getId());
        List<AccommodationResponse> accommodationsInfo = accommodations.stream()
                .map(accommodation -> new AccommodationResponse(
                        accommodation.getId(),
                        accommodation.getAccommodationPrice(),
                        accommodation.getAccommodationName(),
                        accommodation.getGuestCapacity(),
                        accommodation.getAddress(),
                        accommodation.getCity(),
                        accommodation.getZipCode(),
                        accommodation.getType()
                )).toList();
        return new TypeResponse(type.getId(), type.getTypeName(),  type.getDescription(),  accommodationsInfo);
    }
}
