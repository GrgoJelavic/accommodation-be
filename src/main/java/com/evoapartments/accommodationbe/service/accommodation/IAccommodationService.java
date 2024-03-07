package com.evoapartments.accommodationbe.service.accommodation;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IAccommodationService {

    List<Accommodation> getAllAccommodations();

    List<String> getAllAccommodationNames();

    Optional<Accommodation> getAccommodationById(Long accommodationId);

    List<Accommodation> getAccommodationsByTypeId(Long accommodationTypeId);

    byte[] getAccommodationMainPhotoByAccommodationId(Long accommodationId) throws SQLException;

    Accommodation addNewAccommodation(Long accommodationTypeId, BigDecimal accommodationPrice, MultipartFile photo,
                                      String accommodationName, Integer guestCapacity, String address, String city,
                                      Integer zipCode, String country) throws IOException, SQLException;

    Accommodation updateAccommodation(Long accommodationId, Long accommodationTypeId, BigDecimal accommodationPrice,
                                      byte[] photoBytes, String accommodationName, Integer guestCapacity, String address,
                                      String city, Integer zipCode, String country);

    void deleteAccommodation(Long accommodationId);
}
