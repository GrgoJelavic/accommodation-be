package com.evoapartments.accommodationbe.service.calendar;

import com.evoapartments.accommodationbe.domain.calendar.EventDTO;
import com.evoapartments.accommodationbe.domain.calendar.FreeBusyDTO;
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

    List<Event> getCalendarEventsByPeriod(String startDate, String endDate, String calendarId1) throws IOException;

    List<Event> getCalendarEvents(String calendarId) throws IOException;

    String authorize() throws Exception;

    List<AccommodationResponse> getAvailableAccommodationsFromCalendarFreeBusy(FreeBusyDTO freeBusyDTO) throws IOException, SQLException;

    String scheduleReservationEventToGoogleCalendar(String calendarId, EventDTO e) throws IOException, GeneralSecurityException;

    List<List<Event>> getAccommodationAllCalendarsEvents(String startDate, String endDate, String accommodationId) throws IOException;

    Boolean getAccommodationAvailabilityFromCalendarsFreeBusy(FreeBusyDTO request, String accommodationId) throws IOException;
}
