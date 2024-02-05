package com.evoapartments.accomodationbe.response;


import com.evoapartments.accomodationbe.model.AccommodationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class AccommodationResponse {
    private Long id;
    private AccommodationType accommodationType;
    private BigDecimal accommodationPrice;
    private boolean isReserved;
    private String photo;
    private List<AccommodationResponse> reservations;

    public AccommodationResponse(Long id, AccommodationType accommodationType, BigDecimal accommodationPrice, boolean isReserved,
                                 byte[] photoBytes, List<AccommodationResponse> reservations) {
        this.id = id;
        this.accommodationType = accommodationType;
        this.accommodationPrice = accommodationPrice;
        this.isReserved = isReserved;
        this.photo = photo != null ? Base64.encodeBase64String(photoBytes) : null;
        this.reservations = reservations;

    }


}
