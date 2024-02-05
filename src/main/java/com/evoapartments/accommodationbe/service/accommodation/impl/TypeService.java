package com.evoapartments.accommodationbe.service.accommodation.impl;

import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.exception.RoleAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.accommodation.Type;
import com.evoapartments.accommodationbe.repository.accommodation.TypeRepository;
import com.evoapartments.accommodationbe.service.accommodation.ITypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeService implements ITypeService {
    private final TypeRepository accommodationTypeRepository;

    public List<Type> getAllAccommodationTypes(){
        return accommodationTypeRepository.findAll();
    }

    @Override
    public List<Type> getAllAccommodationTypesWithAccommodations() {
        return accommodationTypeRepository.findAll();
    }

    public Optional<Type> getAccommodationTypeById(Long id) {
        return Optional.of(accommodationTypeRepository.findById(id).get());
    }

    public List<String> getAllAccommodationTypeNames(){
        return accommodationTypeRepository.getAllAccommodationTypeNames();
    }

    @Override
    public Type createAccommodationType(Type type) {
        if(accommodationTypeRepository.existsByTypeName(type.getTypeName())){
            throw new RoleAlreadyExistsException(type.getTypeName() + " type already exists");
        }
        return accommodationTypeRepository.save(type);
    }

    @Override
    public Type updateAccommodationType(Long typeId, String typeName, String description) {
        Type accommodationType = accommodationTypeRepository.findById(typeId).orElseThrow(
                () -> new ResourceNotFoundException("Accommodation type not found."));
        if (typeName != null)
            accommodationType.setTypeName(typeName);
        if (description != null) {
            accommodationType.setDescription(description);
        }
        return accommodationTypeRepository.save(accommodationType);
    }

    @DeleteMapping("delete/accommodation-type/{typeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void deleteAccommodationType(@PathVariable Long typeId) {
        Optional<Type> theType = accommodationTypeRepository.findById(typeId);
        if (theType.isPresent()){
            accommodationTypeRepository.deleteById(typeId);
        }
    }
}
