package com.evoapartments.accommodationbe.service.calendar;

import com.evoapartments.accommodationbe.domain.calendar.EventDTO;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.google.api.services.calendar.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

public interface IGoogleCalendarService {

    void initializeClient() throws GeneralSecurityException, IOException;

    RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception;

    String oauth2Callback(String code);

    Event createCalendarReservationEvent(EventDTO e);

    String authorize() throws Exception;

    List<AccommodationResponse> getAvailableAccommodationsFromCalendarFreeBusy(String timeMin, String timeMax) throws IOException, SQLException;

    List<AccommodationResponse> getAvailableAccommodationsByTypeFromCalendarFreeBusy(String timeMin, String timeMax, Long typeId) throws IOException;

    String scheduleReservationEventToGoogleCalendar(Long accommodationId, String calendarId, ReservedAccommodation request) throws IOException, GeneralSecurityException;

    Boolean getAccommodationAvailabilityFromCalendarsFreeBusy(String timeMin, String timeMax, Long accommodationId) throws IOException;

    Event updateGoogleCalendarReservationEvent (String calendarId, String eventId, EventDTO updatedEvent) throws IOException;

    void deleteGoogleCalendarReservationEvent(String calendarId, String eventId) throws IOException;
}
