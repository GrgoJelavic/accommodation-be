package com.evoapartments.accommodationbe.repository;

import com.evoapartments.accommodationbe.model.accommodation.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("SELECT DISTINCT a.accommodationName FROM Accommodation a")
    List<String> getDistinctAccommodationNames();

    @Query("SELECT a FROM Accommodation a " +
            "WHERE a.accommodationType.id = :accommodationType AND a.id NOT IN (" +
            "SELECT ar.accommodation.id FROM AccommodationReserved ar " +
            "WHERE ((ar.checkInDate <= :checkOutDate) AND (ar.checkOutDate >= :checkInDate))" +
            ")")
    List<Accommodation> findAvailableAccommodationsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, Long accommodationType);

    List<Accommodation> findAllByAccommodationTypeId(Long typeId);


}
