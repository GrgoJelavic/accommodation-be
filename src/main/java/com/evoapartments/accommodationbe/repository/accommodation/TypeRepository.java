package com.evoapartments.accommodationbe.repository.accommodation;

import com.evoapartments.accommodationbe.domain.accommodation.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {
    @Query("SELECT DISTINCT a.typeName FROM Type a")
    List<String> getAllAccommodationTypeNames();

    boolean existsByTypeName(String typeName);

    Type findTypeById(Long id);
}
