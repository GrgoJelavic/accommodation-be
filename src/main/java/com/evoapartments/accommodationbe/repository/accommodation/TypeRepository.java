package com.evoapartments.accommodationbe.repository;

import com.evoapartments.accommodationbe.model.accommodation.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccommodationTypeRepository extends JpaRepository<Type, Long> {
    @Query("SELECT DISTINCT a.typeName FROM Type a")
    List<String> getAllAccommodationTypeNames();

    boolean existsByTypeName(String typeName);

    Type findAccommodationTypeById(Long id);
}
