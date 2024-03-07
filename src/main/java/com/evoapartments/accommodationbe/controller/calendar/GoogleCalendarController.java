package com.evoapartments.accommodationbe.controller.calendar;

import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.service.calendar.IGoogleCalendarService;
import com.evoapartments.accommodationbe.service.calendar.impl.GoogleCalendarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping()
public class GoogleCalendarController {
    private final IGoogleCalendarService calendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.calendarService = googleCalendarService;
    }

    @GetMapping("/google/login")
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        return calendarService.googleConnectionStatus(request);
    }

    @GetMapping(value = "/google/login", params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam("code") String code) {
        String msg = calendarService.oauth2Callback(code);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PostMapping("/calendar-freebusy/getAvailableAccommodations")
    public ResponseEntity<?> getAvailableAccommodationsCalendarFreeBusy(
            @RequestParam(value = "timeMin") String timeMin,
            @RequestParam(value = "timeMax") String timeMax) throws IOException, SQLException {
        List<AccommodationResponse> accommodations = calendarService.getAvailableAccommodationsFromCalendarFreeBusy(timeMin, timeMax);
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

    @PostMapping("/calendar-freebusy/getAvailableAccommodationsByType")
    public ResponseEntity<?> getAvailableAccommodationsByTypeFromCalendarFreeBusy(
            @RequestParam(value = "timeMin") String timeMin,
            @RequestParam(value = "timeMax") String timeMax,
            @RequestParam(value = "typeId") Long typeId ) throws IOException {
        List<AccommodationResponse> accommodations = calendarService.getAvailableAccommodationsByTypeFromCalendarFreeBusy(
                timeMin, timeMax, typeId);
        return accommodations.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("availableAccommodations", accommodations))
                        .message("The available accommodations fetched by type successfully. List size: " + accommodations.size())
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/calendar-freebusy/isAccommodationAvailable")
    public ResponseEntity<?> getAccommodationAvailabilityFromCalendarFreeBusy(
            @RequestParam(value = "accommodationId") String accommodationId,
            @RequestParam(value = "timeMin") String timeMin,
            @RequestParam(value = "timeMax") String timeMax) throws IOException {
        return calendarService.getAccommodationAvailabilityFromCalendarsFreeBusy(timeMin, timeMax, Long.valueOf(accommodationId))
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