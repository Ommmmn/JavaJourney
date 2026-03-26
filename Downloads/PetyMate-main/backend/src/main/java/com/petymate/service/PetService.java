package com.petymate.service;

import com.petymate.dto.PetDto;
import org.springframework.data.domain.Pageable;
import com.petymate.dto.PagedResponse;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

public interface PetService {
    PetDto.PetResponse createPet(String email, PetDto.CreatePetRequest request, List<MultipartFile> photos);
    PagedResponse<PetDto.PetResponse> getPets(String species, String breed, String gender, String listingType,
                                               String city, String state, String pincode, Integer radiusKm,
                                               Boolean vaccinationStatus, Boolean pedigreeOnly, Boolean hasOwnSpace,
                                               Integer ageMin, Integer ageMax, BigDecimal minPrice, BigDecimal maxPrice,
                                               String sortBy, Pageable pageable, String currentUserEmail);
    PetDto.PetResponse getPetById(Long id, String currentUserEmail);
    PetDto.PetResponse updatePet(Long id, String email, PetDto.CreatePetRequest request);
    void deletePet(Long id, String email);
    List<PetDto.PetResponse> getMyPets(String email);
    void updatePetStatus(Long id, String email, String status);
    PetDto.PetPhotoDto addPhoto(Long petId, String email, MultipartFile file);
    void deletePhoto(Long petId, Long photoId, String email);
}
