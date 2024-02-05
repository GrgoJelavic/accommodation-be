package com.evoapartments.accommodationbe.service.accommodation;

import com.evoapartments.accommodationbe.model.accommodation.Type;

import java.util.List;
import java.util.Optional;

public interface IAccommodationTypeService {

//    List<AccommodationType> getAllAccommodationTypes();

    List<Type> getAllAccommodationTypesWithReservations();

    Optional<Type> getAccommodationTypeById(Long id);

    List<String> getAllAccommodationTypeNames();

    Type createAccommodationType(Type type);

    List<Type> getAccommodationTypes();
}
