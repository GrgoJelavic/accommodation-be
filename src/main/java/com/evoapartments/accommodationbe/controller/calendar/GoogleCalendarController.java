package com.evoapartments.accommodationbe.controller.calendar;

import com.evoapartments.accommodationbe.domain.calendar.EventDTO;
import com.evoapartments.accommodationbe.domain.calendar.FreeBusyDTO;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.service.calendar.impl.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/google")
public class GoogleCalendarController {
    GoogleCalendarService calendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.calendarService = googleCalendarService;
    }

    @GetMapping("/login")
    public RedirectView googleConnectionStatusHandler(HttpServletRequest request) throws Exception {
        return calendarService.googleConnectionStatus(request);
    }

    @GetMapping(value = "/login", params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam("code") String code) {
        String msg = calendarService.oauth2Callback(code);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/calendar/getReservationEvents")
    public ResponseEntity<?> getCalendarEventsHandler(
            @RequestParam(value = "calendarId") String calendarId) throws IOException {
        List<Event> events = calendarService.getCalendarEvents(calendarId);
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("reservationEvents", events.get(0)))
                        .message("The synchronized calendar reservation event list fetched successfully. Size: " + events.size())
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @GetMapping("/calendar/getReservationEventsByPeriod")
    public ResponseEntity<?> getCalendarEventsByPeriodHandler(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "calendarId") String calendarId) throws IOException {
        List<Event> events = calendarService.getCalendarEventsByPeriod(startDate, endDate, calendarId);
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("reservationEvents", events.get(0)))
                        .message("The calendar reservation event list by period fetched successfully. Size: " + events.size())
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @GetMapping("/calendar/getAccommodationReservationEventsByPeriod")
    public ResponseEntity<?> getAccommodationCalendarEventsByPeriodHandler(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "accommodationId") String accommodationId) throws IOException {
        List<?> events = calendarService.getAccommodationAllCalendarsEvents(startDate, endDate, accommodationId);
        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("reservationEvents", events))
                        .message("The accommodation reservation list fetched by period successfully. Total events: " + events.size())
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PostMapping("/calendar/createReservationEvent")
    public ResponseEntity<?> scheduleReservationToGoogleCalendarHandler(@RequestParam(value = "calendarId") String calendarId,
                                                                        @RequestBody EventDTO e) throws IOException, GeneralSecurityException {
            String msg = calendarService.scheduleReservationEventToGoogleCalendar(calendarId, e);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(msg)
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build());
    }

    @PutMapping("/calendar/updateReservationEvent")
    public ResponseEntity<?> updateGoogleCalendarReservationHandler(@RequestParam(value = "calendarId") String calendarId,
                                                                        @RequestParam String eventId,
                                                                        @RequestBody EventDTO e) throws IOException {
        Event event = calendarService.updateGoogleCalendarReservationEvent(calendarId, eventId, e);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(event)
                        .message("The calendar reservation event updated successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/calendar/deleteReservationEvent")
    public ResponseEntity<?> deleteReservationFromGoogleCalendarHandler(@RequestParam(value = "calendarId") String calendarId,
                                                                            @RequestParam String eventId) throws IOException {
            Event deletedEvent = calendarService.deleteGoogleCalendarReservationEvent(calendarId, eventId);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(deletedEvent)
                            .message("The calendar reservation event deleted successfully.")
                            .status(HttpStatus.NO_CONTENT)
                            .statusCode(HttpStatus.NO_CONTENT.value())
                            .build());
    }

    @PostMapping("/calendar-freebusy/getAvailableAccommodations")
    public ResponseEntity<?> getAllAccommodationsCalendarFreeBusyInfosHandler(
            @RequestBody FreeBusyDTO request) throws IOException {
        List<AccommodationResponse> accommodations = calendarService.getAvailableAccommodationsFromCalendarFreeBusy(request);
        return accommodations.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("availableAccommodations", accommodations))
                        .message("The available accommodations fetched successfully. List size: " + accommodations.size())
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/calendar-freebusy/isAccommodationAvailable")
    public ResponseEntity<?> getAccommodationAvailabilityFromCalendarFreeBusy(
            @RequestParam(value = "accommodationId") String accommodationId,
            @RequestBody FreeBusyDTO request) throws IOException {
        return calendarService.getAccommodationAvailabilityFromCalendarsFreeBusy(request, accommodationId)
                ? ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("isAvailable", true))
                        .message("The accommodation is available for the requested dates.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build())
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("isAvailable", false))
                        .message("The accommodation is not available for the requested dates.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}