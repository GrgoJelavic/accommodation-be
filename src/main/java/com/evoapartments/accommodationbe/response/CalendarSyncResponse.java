package com.evoapartments.accommodationbe.response;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CalendarSyncResponse {
    private Long id;
    private String  description;
    private String googleCalendarId;
    private String syncType;
    private Long accommodationId;
    private String accommodationName;

    public CalendarSyncResponse(Long id, String description, String googleCalendarId, String syncType, Accommodation accommodation) {
        this.id = id;
        this.description = description;
        this.googleCalendarId = googleCalendarId;
        this.syncType = syncType;
        this.accommodationId = accommodation.getId();
        this.accommodationName = accommodation.getAccommodationName();
    }
}
