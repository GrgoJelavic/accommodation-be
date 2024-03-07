package com.evoapartments.accommodationbe.service.calendar.impl;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.repository.calendar.CalendarSyncRepository;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import com.evoapartments.accommodationbe.service.calendar.ICalendarSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarSyncService implements ICalendarSyncService {
    private final CalendarSyncRepository calendarSyncRepository;
    private final IAccommodationService accommodationService;

    @Override
    public List<CalendarSync> getAllSynchronizedCalendars() {
        return calendarSyncRepository.findAll();
    }

    @Override
    public List<CalendarSync> getAllSynchronizedCalendarsByAccommodationId(Long accommodationId) {
        return calendarSyncRepository.findByAccommodationId(accommodationId);
    }

    @Override
    public String getGoogleCalendarIdExportByAccommodationId(Long accommodationId) {
        return calendarSyncRepository.findGoogleCalendarIdExportByAccommodationId(accommodationId);
    }

    @Override
    public CalendarSync createCalendarSynchronization(Long accommodationId, CalendarSync calendarSync) {
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId).orElse(null);
        calendarSync.setAccommodation(accommodation);
        return calendarSyncRepository.save(calendarSync);
    }

    @Override
    public CalendarSync updateCalendarSynchronization(Long calendarSyncId, String description, String googleCalendarId, String syncType) {
        CalendarSync calendarSync = calendarSyncRepository.findById(calendarSyncId).orElseThrow(
                () -> new ResourceNotFoundException("Calendar synchronization not found."));
        if (googleCalendarId != null) {
            calendarSync.setGoogleCalendarId(googleCalendarId);
        }
        if (description != null) {
            calendarSync.setDescription(description);
        }
        if (syncType != null) {
            calendarSync.setSyncType(syncType);
        }
        return calendarSyncRepository.save(calendarSync);
    }

    @Override
    public void deleteCalendarSynchronization(Long calendarSyncId) {
        Optional<CalendarSync> sync = calendarSyncRepository.findById(calendarSyncId);
        if (sync.isPresent()){
            calendarSyncRepository.deleteById(calendarSyncId);
        }
    }
}
