package com.evoapartments.accommodationbe.service.calendar;

import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;

import java.util.List;

public interface ICalendarSyncService {

    List<CalendarSync> getAllSynchronizedCalendarsByAccommodationId(Long accommodationId);

    List<CalendarSync> getAllSynchronizedCalendars();

    String getGoogleCalendarIdExportByAccommodationId(Long accommodationId);

    CalendarSync createCalendarSynchronization(Long accommodationId, CalendarSync calendarSync);

    CalendarSync updateCalendarSynchronization(Long calendarSyncId, String description, String googleCalendarId, String syncType);

    void deleteCalendarSynchronization(Long calendarSyncId);
}
