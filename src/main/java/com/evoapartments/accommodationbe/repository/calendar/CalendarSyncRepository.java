package com.evoapartments.accommodationbe.repository.calendar;

import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalendarSyncRepository extends JpaRepository<CalendarSync, Long > {

    List<CalendarSync> findByAccommodationId(Long accommodationId);

    @Query("SELECT c.googleCalendarId FROM CalendarSync c " +
            "WHERE c.accommodation.id = :accommodationId AND c.syncType = 'export'")
    String findGoogleCalendarIdExportByAccommodationId(Long accommodationId);
}
