package com.evoapartments.accommodationbe.service;

import com.evoapartments.accommodationbe.model.accommodation.Accommodation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAccommodationService {
    Accommodation addNewAccommodation(Long accommodationTypeId, BigDecimal accommodationPrice, MultipartFile photo, String accommodationName,
                                      String address, String city, Integer zipCode, String country) throws IOException, SQLException;

    List<String> getAllAccommodationNames();

    List<Accommodation> getAllAccommodations();

    byte[] getAccommodationMainPhotoByAccommodationId(Long accommodationId) throws SQLException;

    void deleteAccommodation(Long accommodationId);

    Accommodation updateAccommodation(Long accommodationId, Long accommodationTypeId, BigDecimal accommodationPrice, byte[] photoBytes,
                                      String accommodationName, String address, String city, Integer zipCode, String country);

    Optional<Accommodation> getAccommodationById(Long accommodationId);

    List<Accommodation> getAvailableAccommodations(LocalDate checkInDate, LocalDate checkOutDate, Long accommodationTypeId);

    List<Accommodation> getAccommodationsByTypeId(Long accommodationTypeId);




}
