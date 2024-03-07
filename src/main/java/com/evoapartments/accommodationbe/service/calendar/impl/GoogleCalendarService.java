package com.evoapartments.accommodationbe.service.calendar.impl;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.calendar.CalendarSync;
import com.evoapartments.accommodationbe.domain.calendar.EventDTO;
import com.evoapartments.accommodationbe.domain.calendar.FreeBusyDTO;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.response.AccommodationResponse;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import com.evoapartments.accommodationbe.service.calendar.ICalendarSyncService;
import com.evoapartments.accommodationbe.service.calendar.IGoogleCalendarService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService implements IGoogleCalendarService {
    private final ICalendarSyncService calendarSyncService;
    private final IAccommodationService accommodationService;

    private final static Log logger = LogFactory.getLog(GoogleCalendarService.class);
    private static final String APPLICATION_NAME = "accommodation-be";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;
    private static com.google.api.services.calendar.Calendar client;

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;
    TokenResponse response;
    @Value("${google.client.client-id}")
    private String clientId;
    @Value("${google.client.client-secret}")
    private String clientSecret;
    @Value("${google.client.redirectUri}")
    private String redirectURI;

    @Override
    public void initializeClient() throws GeneralSecurityException, IOException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        credential = flow.createAndStoreCredential(response, "accommodation-be");
        client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    @Override
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        return new RedirectView(authorize());
    }

    @Override
    public String oauth2Callback(@RequestParam(value = "code") String code) {
        String message;
        try {
            response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            response.setExpiresInSeconds(18000L);
            message = "Created Access token";
            System.out.println(response);
            initializeClient();
        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")." + " Redirecting to google connection status page.");
            message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")." + " Redirecting to google connection status page.";
        }
        return message;
    }

    @Override
    public String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            Details web = new Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setAccessType("offline").setApprovalPrompt("force").build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        System.out.println("cal authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }

    @Override
    public List<AccommodationResponse> getAvailableAccommodationsFromCalendarFreeBusy(String timeMin, String timeMax) throws IOException {
        List<Accommodation> accommodations = accommodationService.getAllAccommodations();
        List<CalendarSync> syncCalendarsList = getAllSynchronizedAccommodationsCalendars(accommodations);
        FreeBusyDTO freeBusyDTO = new FreeBusyDTO();
        freeBusyDTO.setTimeMin(timeMin + "T00:00:00");
        freeBusyDTO.setTimeMax(timeMax + "T00:00:00");
        FreeBusyResponse freeBusyResponse = getCalendarsFreeBusyResponse(syncCalendarsList, freeBusyDTO);
        List<Long> availableIds = getAvailableAccommodationIds(freeBusyResponse, syncCalendarsList);
        List<Accommodation> availableAccommodations = getAvailableAccommodationsFromAvailableIds(accommodations, availableIds);
        return getAvailableAccommodationsResponse(availableAccommodations);
    }

    @Override
    public List<AccommodationResponse> getAvailableAccommodationsByTypeFromCalendarFreeBusy(String timeMin, String timeMax, Long typeId) throws IOException {
        List<AccommodationResponse> availableAccommodations = getAvailableAccommodationsFromCalendarFreeBusy(timeMin, timeMax);
        List<Accommodation> accommodationsByType = accommodationService.getAccommodationsByTypeId(typeId);
        List<AccommodationResponse> availableAccommodationsByType = new ArrayList<>();
        for (AccommodationResponse accommodationResponse : availableAccommodations) {
            for (Accommodation accommodation : accommodationsByType) {
                if (Objects.equals(accommodationResponse.getId(), accommodation.getId())) {
                    availableAccommodationsByType.add(accommodationResponse);
                }
            }
        }
        return availableAccommodationsByType;
    }


    @Override
    public Boolean getAccommodationAvailabilityFromCalendarsFreeBusy(String timeMin, String timeMax, Long accommodationId) throws IOException {
        List<CalendarSync> calendarSyncList = getAccommodationSynchronizedCalendars(accommodationId);
        FreeBusyDTO freeBusyDTO = new FreeBusyDTO();
        freeBusyDTO.setTimeMin(timeMin + "T00:00:00");
        freeBusyDTO.setTimeMax(timeMax + "T00:00:00");
        FreeBusyResponse freeBusyResponse = getCalendarsFreeBusyResponse(calendarSyncList, freeBusyDTO);
        List<Long> availableIds = getAvailableAccommodationIds(freeBusyResponse, calendarSyncList);
        if (!availableIds.isEmpty()) {
            return accommodationId.equals(availableIds.get(0));
        }
        return false;
    }

    public List<Long> getAvailableAccommodationIds(FreeBusyResponse freeBusyResponse, List<CalendarSync> calendarSyncList) {
        Iterator<Map.Entry<String, FreeBusyCalendar>> freeBusyIterator = freeBusyResponse.getCalendars().entrySet().iterator();
        List<Long> busyAccommodationIds = new ArrayList<>();
        List<Long> availableAccommodationIds = new ArrayList<>();
        while (freeBusyIterator.hasNext()) {
            Map.Entry<String, FreeBusyCalendar> entry = freeBusyIterator.next();
            for (CalendarSync calendarSync : calendarSyncList) {
                if (!busyAccommodationIds.contains(calendarSync.getAccommodation().getId())) {
                    if (calendarSync.getGoogleCalendarId().matches(entry.getKey())) {
                        if (!entry.getValue().getBusy().isEmpty() || entry.getValue().containsKey("errors")) {
                            busyAccommodationIds.add(calendarSync.getAccommodation().getId());
                            break;
                        }
                        if (!availableAccommodationIds.contains(calendarSync.getAccommodation().getId())) {
                            availableAccommodationIds.add(calendarSync.getAccommodation().getId());
                        }
                    }
                }
            }
        }
        availableAccommodationIds.removeIf(busyAccommodationIds::contains);
        return availableAccommodationIds;
    }

    public Event createCalendarReservationEvent(EventDTO e) {
        Event event = new Event().setSummary(e.getSummary()).setLocation(e.getLocation()).setDescription(e.getDescription());
        DateTime start = new DateTime(e.getStartDate());
        DateTime end = new DateTime(e.getEndDate());
        EventDateTime startDate = new EventDateTime().setDate(start);
        EventDateTime endDate = new EventDateTime().setDate(end);
        event.setStart(startDate);
        event.setEnd(endDate);
        return event;
    }

    public FreeBusyResponse getCalendarsFreeBusyResponse(List<CalendarSync> syncList, FreeBusyDTO freeBusyDTO) throws IOException {
        FreeBusyRequest request = new FreeBusyRequest();
        List<FreeBusyRequestItem> items = new ArrayList<>();
        for (CalendarSync group : syncList) {
            items.add(new FreeBusyRequestItem().setId(group.getGoogleCalendarId()));
        }
        request.setItems(items);
        request.setTimeMin(DateTime.parseRfc3339(freeBusyDTO.getTimeMin()));
        request.setTimeMax(DateTime.parseRfc3339(freeBusyDTO.getTimeMax()));
        return client.freebusy().query(request).execute();
    }

    public List<CalendarSync> getAllSynchronizedAccommodationsCalendars(List<Accommodation> accommodations) {
        List<CalendarSync> syncCalendarsList = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            syncCalendarsList.addAll(calendarSyncService.getAllSynchronizedCalendarsByAccommodationId(accommodation.getId()));
        }
        return syncCalendarsList;
    }

    public List<CalendarSync> getAccommodationSynchronizedCalendars(Long accommodationId) {
        return calendarSyncService.getAllSynchronizedCalendarsByAccommodationId(accommodationId);
    }

    private List<AccommodationResponse> getAvailableAccommodationsResponse(List<Accommodation> availableAccommodations) {
        return availableAccommodations.stream()
                .map(accommodation -> new AccommodationResponse(
                        accommodation.getId(),
                        accommodation.getAccommodationPrice(),
                        accommodation.getAccommodationName(),
                        accommodation.getGuestCapacity(),
                        accommodation.getAddress(),
                        accommodation.getCity(),
                        accommodation.getZipCode(),
                        accommodation.getType()
                )).toList();
    }

    private List<Accommodation> getAvailableAccommodationsFromAvailableIds(List<Accommodation> accommodations, List<Long> availableIds) {
        List<Accommodation> availableAccommodations = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            if (availableIds.contains(accommodation.getId())) {
                availableAccommodations.add(accommodation);
            }
        }
        return availableAccommodations;
    }

    public String scheduleReservationEventToGoogleCalendar(Long accommodationId, String calendarId, ReservedAccommodation request)
            throws IOException, GeneralSecurityException {
        initializeClient();
        String startDate = String.valueOf(request.getCheckInDate());
        String endDate = String.valueOf(request.getCheckOutDate());
        if (isDateAvailable(calendarId, startDate, endDate)) {
            Accommodation accommodation = accommodationService.getAccommodationById(accommodationId).orElse(null);
            EventDTO reservationEvent = getEventDTO(startDate, endDate, accommodation, request);
            Event event = client.events().insert(calendarId, createCalendarReservationEvent(reservationEvent)).execute();
            return event.getId();
        }
        else {
            return "Dates are not available. Scheduling new reservation event was unsuccessful!";
        }
    }

    private static EventDTO getEventDTO(String startDate, String endDate, Accommodation accommodation, ReservedAccommodation request ) {
        EventDTO reservationEvent = new EventDTO();
        reservationEvent.setStartDate(startDate);
        reservationEvent.setEndDate(endDate);
        reservationEvent.setSummary("Reserved - " + (request.getGuestFullName()));
        reservationEvent.setDescription("Reservation at " + accommodation + " from: " + startDate + " until: "
                + endDate + "\n Adults: " + request.getNumberOfAdults() + ", Children: " + request.getTotalNumberOfGuests()
                + ", total:" + request.getTotalNumberOfGuests());
        reservationEvent.setLocation(accommodation.getAddress() + ", " + accommodation.getZipCode() + ", "
                + accommodation.getCity() + ", " + accommodation.getCountry());
        return reservationEvent;
    }

    private boolean isDateAvailable(String calendarId, String startDate, String endDate) throws IOException{
        DateTime dateMin = new DateTime(startDate + "T00:00:00+00:00");
        LocalDate dateEnd = LocalDate.parse(endDate).minusDays(1);
        DateTime dateMax = new DateTime(dateEnd + "T23:59:59+00:00");
        List<Event> events = client.events().list(calendarId).setTimeMin(dateMin).setTimeMax(dateMax).execute().getItems();
        return events.isEmpty();
    }

    public void deleteGoogleCalendarReservationEvent(String calendarId, String eventId) throws IOException {
        try {
            client.events().delete(calendarId, eventId).execute();
        } catch (IOException e) {
            throw new IOException("Failed to delete the Calendar Reservation Event: " + e.getMessage());
        }
    }

    public Event updateGoogleCalendarReservationEvent(String calendarId, String eventId, EventDTO updatedEvent) throws IOException {
        try {
            Event oldEvent = client.events().get(calendarId, eventId).execute();
            oldEvent.setSummary(updatedEvent.getSummary());
            oldEvent.setDescription(updatedEvent.getDescription());
            oldEvent.setLocation(updatedEvent.getLocation());
            if (updatedEvent.getStartDate() != null) {
                oldEvent.setStart(new EventDateTime().setDateTime(new DateTime(updatedEvent.getStartDate())));
            }
            if (updatedEvent.getEndDate() != null) {
                oldEvent.setEnd(new EventDateTime().setDateTime(new DateTime(updatedEvent.getEndDate())));
            }
            return client.events().patch(calendarId, eventId, oldEvent).execute();
        } catch (IOException e) {
            throw new IOException("Failed to update the Calendar Reservation Event: " + e.getMessage());
        }
    }
}