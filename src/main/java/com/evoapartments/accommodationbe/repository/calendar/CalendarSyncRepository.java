package com.evoapartments.accommodationbe.repository.calendar;

import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarSyncRepository extends JpaRepository<CalendarSync, Long > {

    List<CalendarSync> findByAccommodationId(Long accommodationId);
}
