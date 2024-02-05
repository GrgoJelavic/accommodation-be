package com.evoapartments.accommodationbe.service.accommodation;

import com.evoapartments.accommodationbe.exception.InternalServerException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.repository.accommodation.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final ITypeService accommodationTypeService;

    @Override
    public Accommodation addNewAccommodation(Long accommodationTypeId, BigDecimal accommodationPrice, MultipartFile photoFile,
                                             String accommodationName, Integer guestCapacity,String address, String city,
                                             Integer zipCode, String country) throws IOException, SQLException {
        Accommodation accommodation = new Accommodation();
        accommodation.setType(accommodationTypeService.getAccommodationTypeById(accommodationTypeId).get());
        accommodation.setAccommodationPrice(accommodationPrice);
        accommodation.setAccommodationName(accommodationName);
        accommodation.setGuestCapacity(guestCapacity);
        accommodation.setAddress(address);
        accommodation.setCity(city);
        accommodation.setZipCode(zipCode);
        accommodation.setCountry(country);
        if (!photoFile.isEmpty()){
            byte[] photoBytes = photoFile.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            accommodation.setPhoto(photoBlob);
        }
        return accommodationRepository.save(accommodation);
    }

    @Override
    public List<String> getAllAccommodationNames(){
        return accommodationRepository.getDistinctAccommodationNames();
    }

    @Override
    public void deleteAccommodation(Long accommodationId) {
        Optional<Accommodation> theAccommodation = accommodationRepository.findById(accommodationId);
        if (theAccommodation.isPresent()){
            accommodationRepository.deleteById(accommodationId);
        }
    }

    @Override
    public Accommodation updateAccommodation(Long accommodationId, Long accommodationTypeId, BigDecimal accommodationPrice,
                                             byte[] photoBytes, String accommodationName, Integer gustCapacity,
                                             String address, String city, Integer zipCode, String country) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(()-> new ResourceNotFoundException("Accommodation not found."));
        if (accommodationTypeId != null) {
            accommodation.setType(accommodationTypeService.getAccommodationTypeById(accommodationTypeId).get());
         }
        if (accommodationPrice != null) {
            accommodation.setAccommodationPrice(accommodationPrice);
        }
        if(photoBytes != null && photoBytes.length > 0) {
            try {
                accommodation.setPhoto(new SerialBlob(photoBytes));
            }catch (SQLException ex) {
                throw new InternalServerException("Error updating accommodation");
            }
        }
        if (accommodationName != null) {
            accommodation.setAccommodationName(accommodationName);
        }
        if (gustCapacity != null) {
            accommodation.setGuestCapacity(gustCapacity);
        }
        if (address != null) {
            accommodation.setAddress(address);
        }
        if (city != null) {
            accommodation.setCity(city);
        }
        if (zipCode != null) {
            accommodation.setZipCode(zipCode);
        }
        if (country != null) {
            accommodation.setCountry(country);
        }
        return accommodationRepository.save(accommodation);
    }

    @Override
    public Optional<Accommodation> getAccommodationById(Long accommodationId) {
        return accommodationRepository.findById(accommodationId);
    }

    @Override
    public List<Accommodation> getAvailableAccommodations(LocalDate checkInDate, LocalDate checkOutDate, Long accommodationTypeId) {
        return accommodationRepository.findAvailableAccommodationsByDatesAndType(checkInDate, checkOutDate, accommodationTypeId);
    }

    @Override
    public List<Accommodation> getAccommodationsByTypeId(Long accommodationTypeId) {
        return accommodationRepository.findAllByTypeId(accommodationTypeId);
    }

    @Override
    public List<Accommodation> getAllAccommodations(){
        return accommodationRepository.findAll();
    }

    @Override
    @Transactional
    public byte[] getAccommodationMainPhotoByAccommodationId(Long accommodationId) throws SQLException {
        Optional<Accommodation> theAccommodation = accommodationRepository.findById(accommodationId);
        if (theAccommodation.isEmpty()){
            throw new ResourceNotFoundException("Sorry, accommodation not found!");
        }
        Blob photoBlob = theAccommodation.get().getPhoto();
        if (photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }
}
