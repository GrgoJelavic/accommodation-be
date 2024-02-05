package com.evoapartments.accommodationbe.response;

import com.evoapartments.accommodationbe.domain.accommodation.Type;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class AccommodationResponse {
    private Long id;
    private BigDecimal accommodationPrice;
    private boolean isReserved;
    private String photo;
    private String accommodationName;
    private Integer guestCapacity;
    private String address;
    private String city;
    private Integer zipCode;
    private String country;
    private Type accommodationType;
    private List<ReservedAccommodationResponse> reservations;

    public AccommodationResponse(Long id,  BigDecimal accommodationPrice,String accommodationName, Integer guestCapacity,
                                 String address, String city, Integer zipCode, String country, Type accommodationType) {
        this.id = id;
        this.accommodationPrice = accommodationPrice;
        this.accommodationName = accommodationName;
        this.guestCapacity = guestCapacity;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.accommodationType = accommodationType;
    }

    public AccommodationResponse(Long id, BigDecimal accommodationPrice, boolean reserved, byte[] photoBytes,
                                 String accommodationName, Integer guestCapacity, String address, String city,
                                 Integer zipCode, String country, Type accommodationType,
                                 List<ReservedAccommodationResponse> reservations) {
        this.id = id;
        this.accommodationPrice = accommodationPrice;
        this.photo = photo != null ? Base64.encodeBase64String(photoBytes) : null;
        this.accommodationName = accommodationName;
        this.guestCapacity = guestCapacity;
        this.isReserved = reserved;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.reservations = reservations;
        this.accommodationType = accommodationType;
    }

    public AccommodationResponse(Long id, BigDecimal accommodationPrice, String accommodationName, Integer guestCapacity,
                                 String address, String city, Integer zipCode, Type accommodationType) {
        this.id = id;
        this.accommodationPrice = accommodationPrice;
        this.accommodationName = accommodationName;
        this.guestCapacity = guestCapacity;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.accommodationType = accommodationType;
    }
}
