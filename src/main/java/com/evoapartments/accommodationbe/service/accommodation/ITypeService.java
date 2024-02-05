package com.evoapartments.accommodationbe.service.accommodation;

import com.evoapartments.accommodationbe.domain.accommodation.Type;

import java.util.List;
import java.util.Optional;

public interface ITypeService {

    List<Type> getAllAccommodationTypes();

    List<Type> getAllAccommodationTypesWithAccommodations();

    Optional<Type> getAccommodationTypeById(Long id);

    List<String> getAllAccommodationTypeNames();

    Type createAccommodationType(Type type);

    Type updateAccommodationType(Long typeId, String typeName, String description);

    void deleteAccommodationType(Long typeId);
}
