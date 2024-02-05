package com.evoapartments.accommodationbe.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccommodationTypeResponse {
    private Long id;
    private String accommodationType;
    private String description;
    private List<AccommodationResponse> accommodations;

    public AccommodationTypeResponse(Long id, String typeName, String description, List<AccommodationResponse> accommodations) {
        this.id = id;
        this.accommodationType = typeName;
        this.description = description;
        this.accommodations = accommodations;
    }

    public AccommodationTypeResponse(Long id, String typeName, String description) {
        this.id = id;
        this.accommodationType = typeName;
        this.description = description;
    }
}
