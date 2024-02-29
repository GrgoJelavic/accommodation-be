package com.evoapartments.accommodationbe.controller.calendar;

import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;
import com.evoapartments.accommodationbe.exception.TypeAlreadyExistsException;
import com.evoapartments.accommodationbe.response.CalendarSyncResponse;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.service.calendar.ICalendarSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar-synchronization")
public class CalendarSyncController {
    private final ICalendarSyncService calendarSyncService;

    @GetMapping("/all-calendars")
    public ResponseEntity<HttpResponse> getAllSynchronizedCalendars(){
        List<CalendarSync> calendarSyncList = calendarSyncService.getAllSynchronizedCalendars();
        List<CalendarSyncResponse> syncResponses = getCalendarSyncResponses(calendarSyncList);
        return calendarSyncList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("synchronizedCalendars", syncResponses, "size", syncResponses.size()))
                        .message("All synchronized calendars list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @GetMapping("/{accommodationId}/all-calendars")
    public ResponseEntity<HttpResponse> getAccommodationSynchronizedCalendars(@PathVariable Long accommodationId){
        List<CalendarSync> calendarList = calendarSyncService.getAllSynchronizedCalendarsByAccommodationId(accommodationId);
        List<CalendarSyncResponse> syncResponses = getCalendarSyncResponses(calendarList);
        return calendarList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("synchronizedCalendars", syncResponses, "size", syncResponses.size()))
                        .message("Synchronized calendars list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PostMapping("{accommodationId}/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCalendarSynchronization(@PathVariable Long accommodationId, @RequestBody CalendarSync calendarSync){
        try {
            CalendarSync calendar = calendarSyncService.createCalendarSynchronization(accommodationId, calendarSync);
            CalendarSyncResponse syncResponse = getCalendarSyncResponse(calendar);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("newCalendarSynchronization", syncResponse))
                            .message("New calendar synchronization created successfully.")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build());
        } catch (TypeAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/{calendarSyncId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> updateCalendarSynchronization(@PathVariable Long calendarSyncId,
                                                                      @RequestParam(required = false) String googleCalendarId,
                                                                      @RequestParam(required = false) String description,
                                                                      @RequestParam(required = false) String syncType) {
        CalendarSync sync = calendarSyncService.updateCalendarSynchronization(calendarSyncId, description, googleCalendarId, syncType);
        CalendarSyncResponse syncResponse = getCalendarSyncResponse(sync);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("updatedSynchronizedCalendar", syncResponse))
                        .message("The calendar synchronization updated successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/{calendarSyncId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> deleteCalendarSynchronization(@PathVariable Long calendarSyncId){
        calendarSyncService.deleteCalendarSynchronization(calendarSyncId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("The calendar synchronization was deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build());
    }

    private CalendarSyncResponse getCalendarSyncResponse(CalendarSync calendar) {
        return new CalendarSyncResponse(calendar.getId(), calendar.getDescription(), calendar.getGoogleCalendarId(),
                calendar.getSyncType(), calendar.getAccommodation());
    }

    private List<CalendarSyncResponse> getCalendarSyncResponses(List<CalendarSync> calendarSyncList) {
        List<CalendarSyncResponse> calendarSyncResponses = new ArrayList<>();
        for (CalendarSync calendarSync : calendarSyncList) {
            calendarSyncResponses.add(getCalendarSyncResponse(calendarSync));
        }
        return calendarSyncResponses;
    }
}
