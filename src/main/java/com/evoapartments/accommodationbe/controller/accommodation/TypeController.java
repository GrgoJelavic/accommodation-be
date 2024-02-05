package com.evoapartments.accommodationbe.resource.accommodation;

import com.evoapartments.accommodationbe.exception.RoleAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.accommodation.Type;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.response.TypeResponse;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import com.evoapartments.accommodationbe.service.accommodation.ITypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodation-types")
public class TypeController {

    private final ITypeService accommodationTypeService;
    private final IAccommodationService accommodationService;

    @GetMapping("/all-types")
    public List<Type> getAllAccommodationTypes(){
        return accommodationTypeService.getAllAccommodationTypes();
    }

    //check if this endpoint is necessary
    @GetMapping("/all-types-with-accommodations")
        public ResponseEntity<List<TypeResponse>> getAllAccommodationTypesWithAccommodations(){
        List<Type> types = accommodationTypeService.getAllAccommodationTypesWithAccommodations();
        List<TypeResponse> accommodationTypeResponses = new ArrayList<>();
        for (Type type : types){
            TypeResponse accommodationTypeResponse = getAccommodationTypesWithAccommodationsResponse(type);
            accommodationTypeResponses.add(accommodationTypeResponse);
        }
        return ResponseEntity.ok(accommodationTypeResponses);
    }

    @GetMapping("/type-names")
    public List<String> getAccommodationTypeNames(){
        return accommodationTypeService.getAllAccommodationTypeNames();
    }

    @PostMapping("/create-new-accommodation-type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createAccommodationType(@RequestBody Type type){
        try {
            accommodationTypeService.createAccommodationType(type);
            return ResponseEntity.ok("New accommodation type created successfully");
        } catch (RoleAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/update/{typeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TypeResponse> updateAccommodationType(@PathVariable Long typeId,
        @RequestParam(required = false) String typeName, @RequestParam(required = false) String description) {
        Type theType = accommodationTypeService.updateAccommodationType(typeId, typeName, description);
        TypeResponse typeResponse = getAccommodationTypeResponse(theType);
        return ResponseEntity.ok(typeResponse);
    }

    @DeleteMapping("/delete/accommodation-type/{typeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAccommodationType(@PathVariable Long typeId){
        accommodationTypeService.deleteAccommodationType(typeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
